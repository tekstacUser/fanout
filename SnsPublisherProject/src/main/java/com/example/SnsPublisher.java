package com.example;


import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

public class SnsPublisher {

    public static void main(String[] args) {
        SnsClient snsClient = SnsClient.create();

        String message = "{\"order_id\":1234,\"file_url\":\"https://s3.amazonaws.com/<BUCKET_URL>/invoice123.pdf\"}";

        PublishRequest request = PublishRequest.builder()
                .topicArn("<SNS_TOPIC_ARN>")
                .message(message)
                .build();

        snsClient.publish(request);

        System.out.println("Message published to SNS");
    }
}

