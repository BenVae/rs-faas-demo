package faas

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers

import faas.ApiHandler
import faas.APIGatewayProxyHandler

class MainSpec extends AnyWordSpec with Matchers {
  "Given empty input" should {
    "return 200 with body serializable Update" in {
      ApiHandler.handle(
        ScalaApiGatewayEvent(body = "{\"message\":{\"text\":\"Hello world! This is a rs-faas-demo.\"}}"),
        ScalaContext()
      ) shouldBe ScalaResponse(
        body = "Hello world! This is a rs-faas-demo.",
        headers = Map("Content-Type" -> "text/plain")
      )
    }

    "return 404 with undeserializable Update" in {
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
