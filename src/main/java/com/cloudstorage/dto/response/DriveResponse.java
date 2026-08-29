package com.cloudstorage.dto.response;

import java.util.List;

public class DriveResponse {

    private List<FolderResponse> folders;

    private List<FileResponse> files;

    public DriveResponse() {
    }

    public DriveResponse(
            List<FolderResponse> folders,
            List<FileResponse> files) {

        this.folders = folders;
        this.files = files;
    }

    public List<FolderResponse> getFolders() {
        return folders;
    }

    public void setFolders(
            List<FolderResponse> folders) {

        this.folders = folders;
    }

    public List<FileResponse> getFiles() {
        return files;
    }

    public void setFiles(
            List<FileResponse> files) {

        this.files = files;
    }
}