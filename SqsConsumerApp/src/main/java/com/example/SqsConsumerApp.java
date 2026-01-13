package com.example;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class SqsConsumerApp {

    private static final String QUEUE_URL = "https://sqs.ap-south-1.amazonaws.com/272337084608/sns-to-sqs-queue";

    public static void main(String[] args) {

        SqsClient sqsClient = SqsClient.builder().build();

        System.out.println("Waiting for messages...");

        while (true) {

            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(QUEUE_URL)
                    .maxNumberOfMessages(1)
                    .waitTimeSeconds(10)
                    .build();

            ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);

            for (Message message : response.messages()) {

                System.out.println("Message received:");
                System.out.println(message.body());

                // Delete message after processing
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .receiptHandle(message.receiptHandle())
                        .build());
            }
        }
    }
}
