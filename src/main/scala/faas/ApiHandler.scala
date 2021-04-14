package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import faas.APIGatewayProxyHandler

object ApiHandler {
  def handle(
      request: ScalaRequest
  ): Response = {
    Response(s"Hello world! This is a rs-faas-demo.")
  }  
}
