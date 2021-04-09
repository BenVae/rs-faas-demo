package example

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse}

class Main  {
  def handler(apiGatewayEvent: APIGatewayV2HTTPEvent, context: Context): APIGatewayV2HTTPResponse = {
    println(s"body = ${apiGatewayEvent.getBody()}")
    return APIGatewayV2HTTPResponse.builder()
      .withStatusCode(200)
      .withBody("okay")
      .build()
  }
}