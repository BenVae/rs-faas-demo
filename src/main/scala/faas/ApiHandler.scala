package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import faas.APIGatewayProxyHandler
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import sttp.client3._
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2._
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import scala.collection.JavaConverters._
import com.amazonaws.services.dynamodbv2.model.PutItemResult

object ApiHandler {
  val token = System.getenv("BOT_TOKEN")
  val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
    .standard()
    .withRegion(Regions.EU_CENTRAL_1)
    .build();
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
            val firstImageId = readImage("1").id
            val firstImage = readImage(firstImageId)
            sendImage(userId, firstImage)
            putUser(userId, firstImageId)
          } else {
            sendMessage(userId, text + " isn\'t martian.")
          }
        } else if (update.callback_query.isDefined) {
          val callback_query = update.callback_query.get
          val chatId = callback_query.from.id
          sendMessage(
            chatId,
            "You sent a callback query. That worked. I think."
          )
        }

        ScalaResponse("OK")
      }
    }
  }

  def readImage(id: String): MarsImage = {
    val attributeValues = client
      .getItem(
        "mars_images", {
          scala.collection.JavaConverters
            .mapAsJavaMapConverter(Map("id" -> { new AttributeValue(id) }))
            .asJava
        }
      )
      .getItem()
      .asScala
      .toMap
    val prev =
      if (attributeValues.get("prev").isEmpty)
        None
      else
        Some(attributeValues.get("prev").get.getS())
    val next =
      if (attributeValues.get("next").isEmpty)
        None
      else
        Some(attributeValues.get("next").get.getS())

    MarsImage(
      id = attributeValues.get("_id").get.getS(),
      title = attributeValues.get("title").get.getS(),
      publish_date = attributeValues.get("publish_date").get.getS(),
      url = attributeValues.get("url").get.getS(),
      prev = prev,
      next = next
    )
  }

  def putUser(id: Int, currentImage: String): PutItemResult = {
    client
      .putItem(
        "mars_users", {
          scala.collection.JavaConverters
            .mapAsJavaMapConverter(
              Map(
                "id" -> { new AttributeValue(id.toString) },
                "current_image" -> { new AttributeValue(currentImage) },
                "subscribed" -> { new AttributeValue().withBOOL(false) }
              )
            )
            .asJava
        }
      )
  }

  // TODO: Find out the return type
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

  // TODO: Find out the return type
  def sendImage(chatId: Int, image: MarsImage) = {
    val partialRequestBody =
      Body(
        chatId,
        image.title + "\n" + image.publish_date,
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

  def getImageReplyMarkup(image: MarsImage): InlineKeyboardMarkup = {
    val navWithPrev =
      if (image.prev.isDefined)
        List(InlineKeyboardButton(PREV_IMAGE, PREV_IMAGE))
      else List()
    val navWithNext =
      if (image.next.isDefined)
        List(InlineKeyboardButton(NEXT_IMAGE, NEXT_IMAGE))
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

case class Update(
    message: Option[Message],
    callback_query: Option[CallbackQuery]
)

case class CallbackQuery(
    data: String,
    from: From,
    message: Message
)

case class From(
    id: Int
)

case class Message(
    text: String,
    chat: Chat,
    message_id: Int
)

case class Chat(
    id: Int
)

case class InlineKeyboardMarkup(
    inline_keyboard: List[List[InlineKeyboardButton]]
)

case class InlineKeyboardButton(
    text: String,
    callback_data: String
)

case class Body(
    chat_id: Int,
    caption: String,
    reply_markup: InlineKeyboardMarkup,
    photo: Option[String],
    animation: Option[String]
)

case class MarsImage(
    id: String,
    title: String,
    url: String,
    prev: Option[String],
    next: Option[String],
    publish_date: String
)
