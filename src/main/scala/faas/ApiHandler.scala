package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import faas.APIGatewayProxyHandler
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import sttp.client3._

object ApiHandler {
  val token = System.getenv("BOT_TOKEN")

  def handle(
      event: ScalaApiGatewayEvent,
      context: ScalaContext
  ): ScalaResponse = {

    decode[Update](event.body) match {
      case Left(error) => {
        ScalaResponse("error: " + error.getMessage(), statusCode = 404)
      }
      case Right(update) => {
        val request = basicRequest
          .body(
            Map(
              "chat_id" -> update.message.chat.id.toString,
              "text" -> ("Hello " + update.message.chat.first_name)
            )
          )
          .post(uri"https://api.telegram.org/bot$token/sendMessage")
        val backend = HttpURLConnectionBackend()
        val response = request.send(backend)

        ScalaResponse("OK")
      }
    }
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
