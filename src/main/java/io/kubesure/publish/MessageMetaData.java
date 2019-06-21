package io.kubesure.publish;

public class MessageMetaData {

    private String topic, message, kafkaBrokerUrl; 
    private Boolean isAsync;
    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKafkaBrokerUrl() {
        return this.kafkaBrokerUrl;
    }

    public void setKafkaBrokerUrl(String kafkaBrokerUrl) {
        this.kafkaBrokerUrl = kafkaBrokerUrl;
    }

    public Boolean isIsAsync() {
        return this.isAsync;
    }

    public Boolean getIsAsync() {
        return this.isAsync;
    }

    public void setIsAsync(Boolean isAsync) {
        this.isAsync = isAsync;
    }
}