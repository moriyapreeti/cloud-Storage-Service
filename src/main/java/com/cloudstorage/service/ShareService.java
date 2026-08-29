package com.cloudstorage.service;

import java.util.List;

import com.cloudstorage.dto.request.CreateShareRequest;
import com.cloudstorage.dto.response.ShareResponse;

public interface ShareService {

    ShareResponse shareFile(
            Long fileId,
            CreateShareRequest request,
            String email);

    List<ShareResponse> getFileShares(
            Long fileId,
            String email);

    String updatePermission(
            Long shareId,
            CreateShareRequest request,
            String email);

    String removeShare(
            Long shareId,
            String email);

    List<ShareResponse> getSharedWithMe(
            String email);
}