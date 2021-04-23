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
            val firstImageId = readImage("1").get("prev").get.getS()
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

  def readImage(id: String): Map[String, AttributeValue] = {
    client
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
  def sendImage(chatId: Int, image: Map[String, AttributeValue]) = {
    val url = image.get("url").get.getS()
    val partialRequestForm = Map(
      "chat_id" -> chatId.toString,
      "caption" -> getCaption(image)
    )
    val (command, requestForm) =
      if (isPhoto(url)) {
        ("sendPhoto", partialRequestForm + ("photo" -> url))
      } else {
        ("sendAnimation", partialRequestForm + ("animation" -> url))
      }

    basicRequest
      .body(requestForm)
      .post(uri"$BASE_URL/$command")
      .send(backend)
  }

  def getImageReplyMarkup(image: Map[String, AttributeValue]) {}

  def isPhoto(imageUrl: String): Boolean = {
    isImageFormat(imageUrl, ".jpg") || isImageFormat(imageUrl, ".jpeg")
  }

  def isImageFormat(imageUrl: String, format: String): Boolean = {
    imageUrl.endsWith(format)
  }

  def getCaption(image: Map[String, AttributeValue]): String = {
    image.get("title").get.getS() + "\n" + image.get("publish_date").get.getS()
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
    id: Int,
)

// TODO:
case class MarsImage(
    id: String,
    prev: String
)
