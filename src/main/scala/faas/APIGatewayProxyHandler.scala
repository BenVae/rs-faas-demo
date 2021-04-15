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

    val headers = if (apiGatewayEvent.getHeaders() != null) {
      apiGatewayEvent.getHeaders().asScala.toMap
    } else {
      Map.empty[String, String]
    }

    val scalaResponse = ApiHandler.handle(
      ScalaApiGatewayEvent(
        version = apiGatewayEvent.getVersion(),
        headers = headers,
        body = apiGatewayEvent.getBody()
      ),
      ScalaContext("my-request-id")
    )

    return APIGatewayV2HTTPResponse
      .builder()
      .withStatusCode(scalaResponse.statusCode)
      .withBody(scalaResponse.body)
      .withHeaders(scalaResponse.javaHeaders)
      .build()
  }
}

case class ScalaApiGatewayEvent(
    version: String = "1.0.0",
    headers: Map[String, String] = Map(),
    body: String = ""
)

case class ScalaContext(awsRequestId: String = "", functionName: String = "")

case class ScalaResponse(
    body: String,
    headers: Map[String, String] = Map("Content-Type" -> "text/plain"),
    statusCode: Int = 200
) {
  // TODO: should we have this here?
  def javaHeaders: java.util.Map[String, String] =
    scala.collection.JavaConverters.mapAsJavaMapConverter(headers).asJava
}
