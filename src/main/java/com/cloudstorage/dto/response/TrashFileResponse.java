package com.cloudstorage.dto.response;

import java.time.LocalDateTime;

public class TrashFileResponse {

    private Long id;
    private String fileName;
    private Long fileSize;
    private LocalDateTime deletedAt;

    public TrashFileResponse() {
    }

    public TrashFileResponse(
            Long id,
            String fileName,
            Long fileSize,
            LocalDateTime deletedAt) {

        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(
            LocalDateTime deletedAt) {

        this.deletedAt = deletedAt;
    }
}