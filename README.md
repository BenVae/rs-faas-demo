## Reactive Systems: Functions as a Service - Demo

Used this [tutorial](https://www.varokas.com/aws-lambda-functions-in-scala/)

## Update Lambda

### Build the JAR
```bash
sbt assembly
```

### Deploy it to AWS
```bash
sam deploy --guided
 ```

### Invoke the function (on AWS)
aws lambda invoke --function-name "rs-faas-demo" /dev/stdout
