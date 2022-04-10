package com.myorg.app;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.time.Duration;
import java.util.Map;


final class Payload {
    String version;
    Params queryStringParameters;

    final static class Params {
        String objectKey;
        String mimeType;
    }
}

public class Greeting {
    private static final Region clientRegion = Region.US_EAST_1;
    private static final String bucketName = System.getenv("bucket");
    private static final Gson gson = new Gson();

    public Map<String, Object> onEvent(Map<String, Object> request) {
        System.out.println("received: " + request);
        Payload.Params params = getParams(request);

        if (params == null || params.objectKey == null) {
            return Map.of(
                    "statusCode", 400,
                    "body", "Bad input: objectKey missing",
                    "headers", Map.of("Access-Control-Allow-Origin", "http://localhost:3000"));
        }
        String url = createPresignedUrl(params.objectKey, params.mimeType);

        return Map.of(
                "statusCode", 200,
                "body", url,
                "headers", Map.of("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    private static String createPresignedUrl(String objectKey, String mimeType) {
        S3Presigner presigner = S3Presigner.builder()
                .region(clientRegion)
                .build();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(mimeType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        String url = presignedRequest.url().toString();
        presigner.close();

        return url;
    }

    private Payload.Params getParams(Map<String, Object> request) {
        JsonElement jsonElement = gson.toJsonTree(request);
        return gson.fromJson(jsonElement, Payload.class).queryStringParameters;
    }
}
