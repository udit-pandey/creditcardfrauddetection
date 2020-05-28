package com.credfinancials.dtos;

/***
 * DTO to store kafka topic info.
 */
public class KafkaTopicInfo {
    private String server;
    private String topic;
    private String port;
    private String consumerGroupId;

    public KafkaTopicInfo(String server, String topic, String port, String consumerGroupId) {
        this.server = server;
        this.topic = topic;
        this.port = port;
        this.consumerGroupId = consumerGroupId;
    }

    public KafkaTopicInfo(String server, String topic, String consumerGroupId) {
        this.server = server;
        this.topic = topic;
        this.port = "9092";
        this.consumerGroupId = consumerGroupId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }
}
