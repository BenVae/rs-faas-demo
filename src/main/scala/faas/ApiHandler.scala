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
import com.amazonaws.services.dynamodbv2._;
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model
import scala.collection.JavaConverters._

object ApiHandler {
  val token = System.getenv("BOT_TOKEN")
  val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
    .standard()
    .withRegion(Regions.EU_CENTRAL_1)
    .build();

  def handle(
      event: ScalaApiGatewayEvent,
      context: ScalaContext
  ): ScalaResponse = {
    decode[Update](event.body) match {
      case Left(error) => {
        ScalaResponse("error: " + error.getMessage(), statusCode = 404)
      }
      case Right(update) => {
        if (update.message.text == "/start") {
          val firstImage = readImage("1").get("prev").get.getS()
          val request = basicRequest
            .body(
              Map(
                "chat_id" -> update.message.chat.id.toString,
                "text" -> (firstImage)
              )
            )
            .post(uri"https://api.telegram.org/bot$token/sendMessage")
          val backend = HttpURLConnectionBackend()
          val response = request.send(backend)
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
}

case class Update(
    message: Message
)

case class Message(
    text: String,
    chat: Chat
)

case class Chat(
    id: Int,
    first_name: String
)

// TODO:
case class MarsImage(
    id: String,
    prev: String
)
