package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CdkConfigApp {
    public static void main(final String[] args) {
        App app = new App();

        new CdkConfigStack(app, "CdkConfigStack", StackProps.builder()

                .env(Environment.builder()
                        .region("us-east-1")
                        .build())
                // For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html
                .build());

        app.synth();
    }
}

