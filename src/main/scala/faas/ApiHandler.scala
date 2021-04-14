package faas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.{
  APIGatewayV2HTTPEvent,
  APIGatewayV2HTTPResponse
}
import faas.APIGatewayProxyHandler

object ApiHandler {

  def handle(
    event: ScalaApiGatewayEvent,
    context: ScalaContext
  ): ScalaResponse = {
    ScalaResponse("Hello world! This is a rs-faas-demo.")
  }  
}
