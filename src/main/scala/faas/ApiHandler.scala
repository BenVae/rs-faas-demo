package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import faas._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import sttp.client3._


object ApiHandler {
  val token = System.getenv("BOT_TOKEN")
  
  val backend = HttpURLConnectionBackend()
  val BASE_URL = s"https://api.telegram.org/bot$token"
  val PREV_IMAGE = "« Older Image"
  val NEXT_IMAGE = "Newer Image »"

  def handle(
      event: ScalaApiGatewayEvent,
      context: ScalaContext
  ): ScalaResponse = {
    decode[Update](event.body) match {
      case Left(error) => {
        ScalaResponse("error: " + error.getMessage(), statusCode = 404)
      }
      case Right(update) => {
        if (update.message.isDefined) {
          val message = update.message.get
          val userId = message.chat.id
          val text = message.text
          if (text == "/start") {
            val firstImageId = DB.readImage("1").prev.get
            val firstImage = DB.readImage(firstImageId)
            sendImage(userId, firstImage)
            DB.putUser(userId, firstImageId)
          } else {
            sendMessage(userId, text + " isn\'t martian.")
          }
        } else if (update.callback_query.isDefined) {
          val callback_query = update.callback_query.get
          val chatId = callback_query.from.id
          val messageId = callback_query.message.message_id
          val image = DB.readImage(update.callback_query.get.data)
          updateImage(chatId, messageId, image)
        }
        ScalaResponse("OK")
      }
    }
  }

  def sendMessage(chatId: Int, text: String) = {
    basicRequest
      .body(
        Map(
          "chat_id" -> chatId.toString,
          "text" -> text
        )
      )
      .post(uri"$BASE_URL/sendMessage")
      .send(backend)
  }

  def sendImage(chatId: Int, image: MarsImage) = {
    val partialRequestBody =
      SendImageBody(
        chatId,
        getCaption(image),
        getImageReplyMarkup(image),
        photo = None,
        animation = None
      )

    val (command, requestBody) = if (isPhoto(image.url)) {
      ("sendPhoto", partialRequestBody.copy(photo = Some(image.url)))
    } else {
      ("sendAnimation", partialRequestBody.copy(animation = Some(image.url)))
    }

    basicRequest
      .body(requestBody.asJson.noSpaces)
      .contentType("application/json")
      .post(uri"$BASE_URL/$command")
      .send(backend)
  }

  def updateImage(chatId: Int, messageId: Int, image: MarsImage) = {
    val mediaType = if (isPhoto(image.url)) "photo" else "animation"

    val body = UpdateImageBody(
      chatId,
      messageId,
      Media(`type` = mediaType, media = image.url, caption = getCaption(image)),
      getImageReplyMarkup(image)
    )

    basicRequest
      .body(body.asJson.noSpaces)
      .contentType("application/json")
      .post(uri"$BASE_URL/editMessageMedia")
      .send(backend)
  }

  def getCaption(image: MarsImage) = {
    image.title + "\n" + image.publish_date
  }

  def getImageReplyMarkup(image: MarsImage): InlineKeyboardMarkup = {
    val navWithPrev =
      if (image.prev.isDefined)
        List(InlineKeyboardButton(PREV_IMAGE, image.prev.get))
      else List()
    val navWithNext =
      if (image.next.isDefined)
        List(InlineKeyboardButton(NEXT_IMAGE, image.next.get))
      else List()
    InlineKeyboardMarkup(List(navWithPrev ::: navWithNext))
  }

  def isPhoto(imageUrl: String): Boolean = {
    isImageFormat(imageUrl, ".jpg") || isImageFormat(imageUrl, ".jpeg")
  }

  def isImageFormat(imageUrl: String, format: String): Boolean = {
    imageUrl.endsWith(format)
  }
}
