package com.guru.kantewala.rest.requests.App;

public class InputRequest {
    String salt;
    Object input;
    long timestamp;
    String hash;

    public InputRequest(String random_string, Object input, long timestamp, String hash) {
        this.salt = random_string;
        this.input = input;
        this.timestamp = timestamp;
        this.hash = hash;
    }
}
