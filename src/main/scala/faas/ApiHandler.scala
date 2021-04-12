package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}

object ApiHandler {
  def handle(
      request: APIGatewayV2HTTPEvent,
      context: Context
  ): Response = {
    Response(s"Hello world! This is a rs-faas-demo\n", Map("Content-Type" -> "text/plain"))
  }

  case class Response(
      body: String,
      headers: Map[String, String],
      statusCode: Int = 200
  ) {
    def javaHeaders: java.util.Map[String, String] = scala.collection.JavaConverters.mapAsJavaMapConverter(headers).asJava
  }
}
