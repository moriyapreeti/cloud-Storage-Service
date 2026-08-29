package com.cloudstorage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.cloudstorage.dto.request.CreateFolderRequest;
import com.cloudstorage.dto.request.MoveFolderRequest;
import com.cloudstorage.dto.response.FolderContentsResponse;
import com.cloudstorage.dto.response.FolderResponse;
import com.cloudstorage.service.FolderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(
            FolderService folderService) {

        this.folderService = folderService;
    }

    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            Authentication authentication) {

        FolderResponse response =
                folderService.createFolder(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<FolderResponse>>
    getRootFolders(
            Authentication authentication) {

        return ResponseEntity.ok(
                folderService.getRootFolders(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<FolderResponse>>
    getChildFolders(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderService.getChildFolders(
                        id,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolderResponse> getFolder(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderService.getFolder(
                        id,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FolderResponse> renameFolder(
            @PathVariable Long id,
            @RequestParam String name,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderService.renameFolder(
                        id,
                        name,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderService.deleteFolder(
                        id,
                        authentication.getName()
                )
        );
    }
    @GetMapping("/{id}/contents")
    public ResponseEntity<FolderContentsResponse>
    getFolderContents(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                folderService.getFolderContents(
                        id,
                        authentication.getName()
                )
        );
    }
    @PutMapping("/{id}/move")
    public ResponseEntity<String> moveFolder(
            @PathVariable Long id,
            @RequestBody MoveFolderRequest request,
            Authentication authentication) {

        String response =
                folderService.moveFolder(
                        id,
                        request.getParentId(),
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}