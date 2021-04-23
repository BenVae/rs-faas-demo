package faas

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;

import java.nio.charset.StandardCharsets;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain

object HealthLambdaClient {

  val healthFunction = System.getenv("HEALTH_FUNCTION_ARN")

  def invoke(): String = {
    val invokeRequest = new InvokeRequest()
      .withFunctionName(healthFunction);

    val awsLambda = AWSLambdaClientBuilder
      .standard()
      .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
      .withRegion(Regions.EU_CENTRAL_1)
      .build();

    return new String(
      awsLambda.invoke(invokeRequest).getPayload().array(),
      StandardCharsets.UTF_8
    );
  }
}
