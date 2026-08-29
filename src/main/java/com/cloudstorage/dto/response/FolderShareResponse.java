package com.cloudstorage.dto.response;

import java.time.LocalDateTime;

import com.cloudstorage.entity.SharePermission;

public class FolderShareResponse {

    private Long id;

    private Long folderId;

    private String folderName;

    private String sharedWithEmail;

    private String sharedWithName;

    private SharePermission permission;

    private LocalDateTime createdAt;

    public FolderShareResponse() {
    }

    public FolderShareResponse(
            Long id,
            Long folderId,
            String folderName,
            String sharedWithEmail,
            String sharedWithName,
            SharePermission permission,
            LocalDateTime createdAt) {

        this.id = id;
        this.folderId = folderId;
        this.folderName = folderName;
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

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
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