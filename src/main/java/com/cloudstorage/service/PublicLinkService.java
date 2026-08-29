package com.cloudstorage.service;

import java.util.List;

import com.cloudstorage.dto.request.CreatePublicLinkRequest;
import com.cloudstorage.dto.request.PublicLinkAccessRequest;
import com.cloudstorage.dto.response.PublicLinkAccessResponse;
import com.cloudstorage.dto.response.PublicLinkResponse;

public interface PublicLinkService {

    PublicLinkResponse createLink(
            Long fileId,
            CreatePublicLinkRequest request,
            String email);

    List<PublicLinkResponse> getFileLinks(
            Long fileId,
            String email);

    String disableLink(
            Long linkId,
            String email);

    PublicLinkAccessResponse accessLink(
            String token,
            PublicLinkAccessRequest request);
}