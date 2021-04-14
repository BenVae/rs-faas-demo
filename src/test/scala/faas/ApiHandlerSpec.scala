package faas

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers

import faas.ApiHandler
import faas.APIGatewayProxyHandler

class MainSpec extends AnyWordSpec with Matchers {
  "Given empty input" should {
    "return 200 with body 'okay'" in {
      ApiHandler.handle(
        ScalaApiGatewayEvent(),
        ScalaContext()
      ) shouldBe ScalaResponse(
        body = "Hello world! This is a rs-faas-demo.",
        headers = Map("Content-Type" -> "text/plain")
      )
    }
  }
}
