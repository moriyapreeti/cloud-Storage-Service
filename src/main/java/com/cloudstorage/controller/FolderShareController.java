package com.cloudstorage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.cloudstorage.dto.request.CreateFolderShareRequest;
import com.cloudstorage.dto.response.FolderShareResponse;
import com.cloudstorage.service.FolderShareService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/folder-shares")
public class FolderShareController {

    private final FolderShareService
            folderShareService;

    public FolderShareController(
            FolderShareService folderShareService) {

        this.folderShareService =
                folderShareService;
    }

    @PostMapping("/folders/{folderId}")
    public ResponseEntity<FolderShareResponse>
    shareFolder(
            @PathVariable Long folderId,
            @Valid @RequestBody
            CreateFolderShareRequest request,
            Authentication authentication) {

        FolderShareResponse response =
                folderShareService.shareFolder(
                        folderId,
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping("/folders/{folderId}")
    public ResponseEntity<List<FolderShareResponse>>
    getFolderShares(
            @PathVariable Long folderId,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderShareService.getFolderShares(
                        folderId,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{shareId}")
    public ResponseEntity<String>
    updatePermission(
            @PathVariable Long shareId,
            @Valid @RequestBody
            CreateFolderShareRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderShareService.updatePermission(
                        shareId,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{shareId}")
    public ResponseEntity<String>
    removeShare(
            @PathVariable Long shareId,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderShareService.removeShare(
                        shareId,
                        authentication.getName()
                )
        );
    }
}