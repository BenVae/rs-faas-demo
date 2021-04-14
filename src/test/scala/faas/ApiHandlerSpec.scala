package faas

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import com.amazonaws.services.lambda.runtime.Context
import faas.ApiHandler
import faas.APIGatewayProxyHandler
import faas.TestContext

class MainSpec extends AnyWordSpec with Matchers {
  "Given empty input" should {
    "return 200 with body 'okay'" in {
      ApiHandler.handle(
        APIGatewayV2HTTPEvent
          .builder()
          .withBody("{}")
          .build(),
        new TestContext()
      ) shouldBe Response(
        body = "Hello world! This is a rs-faas-demo.",
        headers = Map("Content-Type" -> "text/plain")
      )
    }
  }
}
