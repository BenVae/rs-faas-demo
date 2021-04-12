package faas

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import com.amazonaws.services.lambda.runtime.Context
import faas.Main
import faas.TestContext

class MainSpec extends AnyWordSpec with Matchers {

  "Given empty input" should {
    "return 200 with body 'okay'" in {
      new Main().handle(
        APIGatewayV2HTTPEvent
          .builder()
          .withBody("{}")
          .build(),
        new TestContext()
      ) shouldBe APIGatewayV2HTTPResponse
        .builder()
        .withStatusCode(200)
        .withBody("okay")
        .build()
    }
  }
}
