package com.cloudstorage.dto.response;

import java.time.LocalDateTime;

public class PublicLinkResponse {

    private Long id;

    private Long fileId;

    private String fileName;

    private String token;

    private String publicUrl;

    private LocalDateTime expiresAt;

    private boolean passwordProtected;

    private boolean active;

    private LocalDateTime createdAt;

    public PublicLinkResponse() {
    }

    public PublicLinkResponse(
            Long id,
            Long fileId,
            String fileName,
            String token,
            String publicUrl,
            LocalDateTime expiresAt,
            boolean passwordProtected,
            boolean active,
            LocalDateTime createdAt) {

        this.id = id;
        this.fileId = fileId;
        this.fileName = fileName;
        this.token = token;
        this.publicUrl = publicUrl;
        this.expiresAt = expiresAt;
        this.passwordProtected =
                passwordProtected;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(
            LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isPasswordProtected() {
        return passwordProtected;
    }

    public void setPasswordProtected(
            boolean passwordProtected) {

        this.passwordProtected =
                passwordProtected;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}
