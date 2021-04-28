![](/1200px-NASA_logo.png)
# The ScalaNasaMarsRobot, a Telegram chat bot.

---
## --> [Click here to try it yourself.](https://t.me/scalanasamarsrobot) <--

---

## References

We used [this](https://www.varokas.com/aws-lambda-functions-in-scala/), and [this](https://www.bks2.com/2019/05/02/hello-scala-aws-lambda/) tutorial.

We rely on [this library](https://github.com/aws/aws-lambda-java-libs/tree/master/aws-lambda-java-events) for lambda java events.

### Update Lambda
#### Build the JAR
```bash
sbt assembly
```

#### Deploy it to AWS
```bash
sam deploy --guided
 ```

#### Invoke the function (on AWS)
```
aws lambda invoke --function-name "rs-faas-demo-RsFaasDemo-ODICW0P95RKL" /dev/stdout
```
