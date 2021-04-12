## Reactive Systems: Functions as a Service - Demo

Used this [tutorial](https://www.varokas.com/aws-lambda-functions-in-scala/)

## Update Lambda

### Build the JAR
```bash
sbt assembly
```

### Deploy it to AWS
```bash
 aws lambda update-function-code --function-name "rs-faas-demo" --zip fileb://target/scala-2.13/rs-faas-demo.jar
 ```

 ### Invoke the function (on AWS)
 aws lambda invoke --function-name "rs-faas-demo" /dev/stdout
