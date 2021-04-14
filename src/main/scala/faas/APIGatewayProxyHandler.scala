package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import scala.collection.JavaConverters._
import faas.ApiHandler
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.LambdaLogger

class APIGatewayProxyHandler {

  def handleRequest(
      apiGatewayEvent: APIGatewayV2HTTPEvent,
      context: Context
  ): APIGatewayV2HTTPResponse = {
    println(s"body = ${apiGatewayEvent.getBody()}")

    val scalaEvent = ScalaApiGatewayEvent(
      version = apiGatewayEvent.getVersion(),
      headers = apiGatewayEvent.getHeaders().asScala.toMap,
      body = apiGatewayEvent.getBody()
    )

    val response = ApiHandler.handle(scalaEvent, context)
    // val response = ScalaResponse(body = "test")


    return APIGatewayV2HTTPResponse
      .builder()
      .withStatusCode(response.statusCode)
      .withBody(response.body)
      .withHeaders(response.javaHeaders)
      .build()
  }
}

case class ScalaApiGatewayEvent(
      version: String,
      headers: Map[String, String],
      body: String
  )

/* class ScalaContext extends Context(
  // TODO: impl
) */

case class ScalaResponse(
      body: String,
      headers: Map[String, String] = Map("Content-Type" -> "text/plain"),
      statusCode: Int = 200
  ) {
    def javaHeaders: java.util.Map[String, String] = scala.collection.JavaConverters.mapAsJavaMapConverter(headers).asJava
  }

