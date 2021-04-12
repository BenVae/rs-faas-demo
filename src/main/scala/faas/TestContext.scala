package faas

import com.amazonaws.services.lambda.runtime.Context

class TestContext extends Context {
  def getAwsRequestId() = "495b12a8-xmpl-4eca-8168-160484189f99"
  def getLogGroupName() = "test_log_group_name"
  def getClientContext(): com.amazonaws.services.lambda.runtime.ClientContext = null
  def getFunctionName() = "rs-faas-demo"
  def getFunctionVersion() = "test_function_version"
  def getIdentity(): com.amazonaws.services.lambda.runtime.CognitoIdentity = null
  def getInvokedFunctionArn() = "test_invoked_function_arn"
  def getLogStreamName(): String = "test_log_stream_name"
  def getLogger(): com.amazonaws.services.lambda.runtime.LambdaLogger = null
  def getMemoryLimitInMB(): Int = 1024
  def getRemainingTimeInMillis(): Int = 1000
}
