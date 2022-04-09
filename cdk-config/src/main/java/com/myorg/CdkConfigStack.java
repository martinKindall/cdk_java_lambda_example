package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;


public class CdkConfigStack extends Stack {
    static int memory = 256;
    static String lambdaHandler = "com.myorg.app.Greeting::onEvent";

    public CdkConfigStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function.Builder.create(this, "myLambda")
                .runtime(Runtime.JAVA_11)
                .architecture(Architecture.X86_64)
                .handler(lambdaHandler)
                .code(Code.fromAsset("../lambda/target/function.jar"))
                .memorySize(memory)
                .timeout(Duration.seconds(10))
                .build();
    }
}
