package com.myorg.app;

import java.time.Duration;
import java.util.Map;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


public class Greeting {
    private static final Region clientRegion = Region.US_EAST_1;
    private static final String bucketName = "just-testing-photos-walrus-code";

    public String onEvent(Map<String, String> event) {
        System.out.println("received: " + event);
        String objectKey = event.get("objectKey");
        if (objectKey == null) {
            throw new RuntimeException("objectKey not provided!");
        }
        createPresignedUrl(objectKey);

        return "Event processed !";
    }

    private static void createPresignedUrl(String objectKey) {
        S3Presigner presigner = S3Presigner.builder()
                .region(clientRegion)
                .build();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType("text/plain")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        String myURL = presignedRequest.url().toString();
        System.out.println("Presigned URL to upload a file to: " +myURL);
        System.out.println("Which HTTP method needs to be used when uploading a file: " +
                presignedRequest.httpRequest().method());

        presigner.close();
    }
}
