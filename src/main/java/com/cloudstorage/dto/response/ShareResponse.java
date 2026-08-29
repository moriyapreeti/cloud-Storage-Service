package com.cloudstorage.dto.response;

import java.time.LocalDateTime;

import com.cloudstorage.entity.SharePermission;

public class ShareResponse {

    private Long id;

    private Long fileId;

    private String fileName;

    private String sharedWithEmail;

    private String sharedWithName;

    private SharePermission permission;

    private LocalDateTime createdAt;

    public ShareResponse() {
    }

    public ShareResponse(
            Long id,
            Long fileId,
            String fileName,
            String sharedWithEmail,
            String sharedWithName,
            SharePermission permission,
            LocalDateTime createdAt) {

        this.id = id;
        this.fileId = fileId;
        this.fileName = fileName;
        this.sharedWithEmail = sharedWithEmail;
        this.sharedWithName = sharedWithName;
        this.permission = permission;
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

    public String getSharedWithEmail() {
        return sharedWithEmail;
    }

    public void setSharedWithEmail(
            String sharedWithEmail) {

        this.sharedWithEmail = sharedWithEmail;
    }

    public String getSharedWithName() {
        return sharedWithName;
    }

    public void setSharedWithName(
            String sharedWithName) {

        this.sharedWithName = sharedWithName;
    }

    public SharePermission getPermission() {
        return permission;
    }

    public void setPermission(
            SharePermission permission) {

        this.permission = permission;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}