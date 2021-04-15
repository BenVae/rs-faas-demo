package faas

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers

import faas.ApiHandler
import faas.APIGatewayProxyHandler

class MainSpec extends AnyWordSpec with Matchers {

  val chatId = System.getenv("CHAT_ID")

  "Given Telegram Update" should {
    "return 200 when serializable Update" in {
      ApiHandler.handle(
        ScalaApiGatewayEvent(body = "{\"message\":{\"text\":\"Hello world! This is a rs-faas-demo.\",\"chat\":{\"id\":"+chatId+",\"first_name\":\"Robert\"}}}"),
        ScalaContext()
      ) shouldBe ScalaResponse(
        body = "OK",
        headers = Map("Content-Type" -> "text/plain")
      )
    }

    "return 404 when undeserializable Update" in {
      ApiHandler.handle(
        ScalaApiGatewayEvent(body = "{{\"text\":\"Hello world! This is a rs-faas-demo.\"}"),
        ScalaContext()
      ) shouldBe ScalaResponse(
        body = "error: expected \" got '{\"text...' (line 1, column 2)",
        headers = Map("Content-Type" -> "text/plain"),
        statusCode = 404
      )
    }
  }
}
