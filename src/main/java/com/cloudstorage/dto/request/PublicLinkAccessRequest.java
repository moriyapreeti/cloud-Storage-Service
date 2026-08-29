package com.cloudstorage.dto.request;

public class PublicLinkAccessRequest {

    private String password;

    public PublicLinkAccessRequest() {
    }

    public PublicLinkAccessRequest(
            String password) {

        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password) {

        this.password = password;
    }
}