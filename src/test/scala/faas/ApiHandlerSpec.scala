package faas

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

import faas.ApiHandler
import faas.APIGatewayProxyHandler

class MainSpec extends AnyWordSpec with Matchers {

  val chatId = System.getenv("CHAT_ID")

  "Given Telegram Update" should {
    "return 200 when serializable Update with message" in {
      ApiHandler.handle(
        ScalaApiGatewayEvent(body =
          Update(
            message = Some(
              Message(text = "Hello world!", chat = Chat(1), message_id = 123)
            ),
            callback_query = None
          ).asJson.noSpaces
        ),
        ScalaContext()
      ) shouldBe ScalaResponse(
        body = "OK",
        headers = Map("Content-Type" -> "text/plain")
      )
    }

    "return 200 when serializable Update with callback_query" in {
      ApiHandler.handle(
        ScalaApiGatewayEvent(body =
          Update(
            callback_query = Some(
              CallbackQuery(
                data = "« Older Image",
                from = From(id = 123123),
                message = Message(
                  text = "Hello world!",
                  chat = Chat(1),
                  message_id = 123
                )
              )
            ),
            message = None
          ).asJson.noSpaces
        ),
        ScalaContext()
      ) shouldBe ScalaResponse(
        body = "OK",
        headers = Map("Content-Type" -> "text/plain")
      )
    }

    "return 404 when undeserializable Update" in {
      ApiHandler.handle(
        ScalaApiGatewayEvent(body =
          "{{\"text\":\"Hello world! This is a rs-faas-demo.\"}"
        ),
        ScalaContext()
      ) shouldBe ScalaResponse(
        body = "error: expected \" got '{\"text...' (line 1, column 2)",
        headers = Map("Content-Type" -> "text/plain"),
        statusCode = 404
      )
    }
  }
}
