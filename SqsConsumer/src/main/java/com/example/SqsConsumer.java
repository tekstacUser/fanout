package com.example;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import software.amazon.awssdk.regions.Region;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;

public class SqsConsumer {

    public static void main(String[] args) {

        System.out.println("SQS Consumer started");

        SqsClient sqs = SqsClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();

        String queueUrl = "https://sqs.<REGION>.amazonaws.com/<ACCOUNT_ID>/<SNS_QUEUE>";
        ObjectMapper mapper = new ObjectMapper();

        while (true) {
            try {
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(10)
                        .build();

                for (Message msg : sqs.receiveMessage(request).messages()) {

                    try {
                        // Parse SNS envelope
                        JsonNode snsEnvelope = mapper.readTree(msg.body());
                        String actualMessage = snsEnvelope.get("Message").asText();

                        // Parse business payload
                        JsonNode businessJson = mapper.readTree(actualMessage);
                        String fileUrl = businessJson.get("file_url").asText();

                        // Download file
                        try (InputStream in = new URL(fileUrl).openStream();
                             FileOutputStream fos = new FileOutputStream("downloaded_invoice.pdf")) {

                            fos.getChannel().transferFrom(
                                    Channels.newChannel(in),
                                    0,
                                    Long.MAX_VALUE
                            );
                        }

                        System.out.println("Message processed successfully");

                        // Delete message
                        sqs.deleteMessage(DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(msg.receiptHandle())
                                .build());

                        System.out.println("Message deleted successfully");

                    } catch (Exception e) {
                        System.out.println("Message processing failed: " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                System.out.println("SQS receive failed: " + e.getMessage());
            }
        }
    }
}

