package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import faas.ApiHandler

class APIGatewayProxyHandler {

  def handleRequest(
      apiGatewayEvent: APIGatewayV2HTTPEvent,
      context: Context
  ): APIGatewayV2HTTPResponse = {
    println(s"body = ${apiGatewayEvent.getBody()}")

    val response = ApiHandler.handle(apiGatewayEvent, context)

    return APIGatewayV2HTTPResponse
      .builder()
      .withStatusCode(response.statusCode)
      .withBody(response.body)
      .withHeaders(response.javaHeaders)
      .build()
  }
}
