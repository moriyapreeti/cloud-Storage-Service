package com.cloudstorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateFolderRequest {

    @NotBlank(message = "Folder name is required")
    @Size(
        max = 100,
        message = "Folder name cannot exceed 100 characters"
    )
    private String name;

    private Long parentId;

    public CreateFolderRequest() {
    }

    public CreateFolderRequest(
            String name,
            Long parentId) {

        this.name = name;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}