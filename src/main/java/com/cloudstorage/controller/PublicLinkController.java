package com.cloudstorage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.cloudstorage.dto.request.CreatePublicLinkRequest;
import com.cloudstorage.dto.request.PublicLinkAccessRequest;
import com.cloudstorage.dto.response.PublicLinkAccessResponse;
import com.cloudstorage.dto.response.PublicLinkResponse;
import com.cloudstorage.service.PublicLinkService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public-links")
public class PublicLinkController {

    private final PublicLinkService
            publicLinkService;

    public PublicLinkController(
            PublicLinkService publicLinkService) {

        this.publicLinkService =
                publicLinkService;
    }

    @PostMapping("/files/{fileId}")
    public ResponseEntity<PublicLinkResponse>
    createLink(
            @PathVariable Long fileId,
            @Valid @RequestBody
            CreatePublicLinkRequest request,
            Authentication authentication) {

        return ResponseEntity
                .status(201)
                .body(
                    publicLinkService.createLink(
                        fileId,
                        request,
                        authentication.getName()
                    )
                );
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<List<PublicLinkResponse>>
    getFileLinks(
            @PathVariable Long fileId,
            Authentication authentication) {

        return ResponseEntity.ok(
                publicLinkService.getFileLinks(
                        fileId,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{linkId}")
    public ResponseEntity<String>
    disableLink(
            @PathVariable Long linkId,
            Authentication authentication) {

        return ResponseEntity.ok(
                publicLinkService.disableLink(
                        linkId,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/access/{token}")
    public ResponseEntity<PublicLinkAccessResponse>accessLink(@PathVariable String token,@RequestBody(required = false)
            PublicLinkAccessRequest request) {

        return ResponseEntity.ok(
                publicLinkService.accessLink(
                        token,
                        request
                )
        );
    }
}