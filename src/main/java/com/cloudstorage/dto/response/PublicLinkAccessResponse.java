package com.cloudstorage.dto.response;

public class PublicLinkAccessResponse {

    private String fileName;

    private String downloadUrl;

    private long expiresInSeconds;

    public PublicLinkAccessResponse() {
    }

    public PublicLinkAccessResponse(
            String fileName,
            String downloadUrl,
            long expiresInSeconds) {

        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(
            long expiresInSeconds) {

        this.expiresInSeconds =
                expiresInSeconds;
    }
}