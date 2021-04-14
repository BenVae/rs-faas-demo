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

    val scalaEvent = ScalaApiGatewayEvent(version = apiGatewayEvent.)

    val response = ApiHandler.handle(Request(, context))

    return APIGatewayV2HTTPResponse
      .builder()
      .withStatusCode(response.statusCode)
      .withBody(response.body)
      .withHeaders(response.javaHeaders)
      .build()
  }
}

case class ScalaRequest(
      event: ScalaApiGatewayEvent,
      context: ScalaContext
  )

case class ScalaApiGatewayEvent(
      version: String,
      headers: Map[String, String],
      body: String
  )

class ScalaContext extends Context(
  // TODO: impl
)

case class Response(
      body: String,
      headers: Map[String, String] = Map("Content-Type" -> "text/plain"),
      statusCode: Int = 200
  ) {
    def javaHeaders: java.util.Map[String, String] = scala.collection.JavaConverters.mapAsJavaMapConverter(headers).asJava
  }

