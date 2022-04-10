package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.apigatewayv2.alpha.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.List;
import java.util.Map;


public class CdkConfigStack extends Stack {
    static int memory = 256;
    static String lambdaHandler = "com.myorg.app.Greeting::onEvent";

    Bucket bucket;
    Function lambda;

    public CdkConfigStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        setupBucket();
        setupLambda();
        setupHttpApi();

        bucket.grantPut(lambda);
    }

    private void setupBucket() {
        bucket = Bucket.Builder.create(this, "MyBucket").build();
        PolicyStatement policyStatement = PolicyStatement.Builder.create()
                .principals(List.of(new AnyPrincipal()))
                .actions(List.of("s3:GetObject"))
                .resources(List.of(bucket.getBucketArn() + "/*"))
                .build();

        bucket.addToResourcePolicy(policyStatement);
    }

    private void setupLambda() {
        lambda = Function.Builder.create(this, "myLambda")
                .runtime(Runtime.JAVA_11)
                .architecture(Architecture.X86_64)
                .handler(lambdaHandler)
                .code(Code.fromAsset("../lambda/target/function.jar"))
                .memorySize(memory)
                .environment(Map.of("bucket", bucket.getBucketName()))
                .timeout(Duration.seconds(10))
                .build();
    }

    private void setupHttpApi() {
        HttpApi httpApi = new HttpApi(this, "HttpApi");
        HttpLambdaIntegration booksIntegration = new HttpLambdaIntegration("BooksIntegration", lambda);
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/getPresignedUrl")
                .methods(List.of(HttpMethod.GET))
                .integration(booksIntegration)
                .build());
    }
}
