package com.alpha.messenger.persistance.model;


public class Message {
    private String from;
    private String body;
    private Long timestamp;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Number getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return String.format(
                "{ \"from\": \"%s\", \"body\": \"%s\", \"timestamp\": %d, \"type\": \"%s\"}",
                this.from, this.body, this.timestamp, this.type);
    }
}
