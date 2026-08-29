package com.cloudstorage.dto.request;

public class MoveFolderRequest {

    private Long parentId;

    public MoveFolderRequest() {
    }

    public MoveFolderRequest(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}