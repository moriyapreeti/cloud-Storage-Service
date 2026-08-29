package com.cloudstorage.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

public class CreatePublicLinkRequest {

    @Future(
        message = "Expiry must be a future date"
    )
    private LocalDateTime expiresAt;

    @Size(
        min = 4,
        max = 50,
        message = "Password must be between 4 and 50 characters"
    )
    private String password;

    public CreatePublicLinkRequest() {
    }

    public CreatePublicLinkRequest(
            LocalDateTime expiresAt,
            String password) {

        this.expiresAt = expiresAt;
        this.password = password;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(
            LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password) {
        this.password = password;
    }
}