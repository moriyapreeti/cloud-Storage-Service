package com.cloudstorage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cloudstorage.dto.request.CreateShareRequest;
import com.cloudstorage.dto.response.ShareResponse;
import com.cloudstorage.service.ShareService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/shares")
public class ShareController {

    private final ShareService shareService;

    public ShareController(
            ShareService shareService) {

        this.shareService = shareService;
    }

    @PostMapping("/files/{fileId}")
    public ResponseEntity<ShareResponse> shareFile(
            @PathVariable Long fileId,
            @Valid @RequestBody CreateShareRequest request,
            Authentication authentication) {

        ShareResponse response =
                shareService.shareFile(
                        fileId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<List<ShareResponse>>
    getFileShares(
            @PathVariable Long fileId,
            Authentication authentication) {

        return ResponseEntity.ok(
                shareService.getFileShares(
                        fileId,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{shareId}")
    public ResponseEntity<String> updatePermission(
            @PathVariable Long shareId,
            @Valid @RequestBody CreateShareRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                shareService.updatePermission(
                        shareId,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{shareId}")
    public ResponseEntity<String> removeShare(
            @PathVariable Long shareId,
            Authentication authentication) {

        return ResponseEntity.ok(
                shareService.removeShare(
                        shareId,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/shared-with-me")
    public ResponseEntity<List<ShareResponse>>
    getSharedWithMe(
            Authentication authentication) {

        return ResponseEntity.ok(
                shareService.getSharedWithMe(
                        authentication.getName()
                )
        );
    }
}