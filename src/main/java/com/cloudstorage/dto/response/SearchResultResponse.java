package com.cloudstorage.dto.response;

public class SearchResultResponse {

    private Long id;

    private String name;

    private String type;

    private String contentType;

    private Long parentFolderId;

    private String permission;
    private Long size;

    public SearchResultResponse() {
    }

    public SearchResultResponse(
            Long id,
            String name,
            String type,
            String contentType,
            Long parentFolderId,
            String permission,
            Long size) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.contentType = contentType;
        this.parentFolderId = parentFolderId;
        this.permission = permission;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(Long parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
}