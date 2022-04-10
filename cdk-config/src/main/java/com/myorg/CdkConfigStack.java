package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.Map;


public class CdkConfigStack extends Stack {
    static int memory = 256;
    static String lambdaHandler = "com.myorg.app.Greeting::onEvent";

    public CdkConfigStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Bucket bucket = Bucket.Builder.create(this, "MyBucket").build();

        var myLambda = Function.Builder.create(this, "myLambda")
                .runtime(Runtime.JAVA_11)
                .architecture(Architecture.X86_64)
                .handler(lambdaHandler)
                .code(Code.fromAsset("../lambda/target/function.jar"))
                .memorySize(memory)
                .environment(Map.of("bucket", bucket.getBucketName()))
                .timeout(Duration.seconds(10))
                .build();

        bucket.grantPut(myLambda);
    }
}
