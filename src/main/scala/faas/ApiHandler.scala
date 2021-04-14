package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import faas.APIGatewayProxyHandler
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object ApiHandler {

  def handle(
    event: ScalaApiGatewayEvent,
    context: ScalaContext
  ): ScalaResponse = {
   
    decode[Update](event.body) match {
      case Left(error) => {
        ScalaResponse("error: " + error.getMessage(), statusCode = 404)
      }
      case Right(update) => {
        ScalaResponse(update.message.text)
      }
    }
  }  
}

case class Update (
  message: Message
)

case class Message (
  text: String
)