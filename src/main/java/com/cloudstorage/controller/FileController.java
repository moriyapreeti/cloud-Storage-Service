package com.cloudstorage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cloudstorage.dto.response.FileResponse;
import com.cloudstorage.dto.response.TrashFileResponse;
import com.cloudstorage.service.FileService;
import com.cloudstorage.dto.request.MoveFileRequest;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(
            FileService fileService) {

        this.fileService = fileService;
    }

   @PostMapping(value = "/upload",consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
    	
    	  System.out.println("========== UPLOAD CONTROLLER ==========");
    	    System.out.println("File name: " + file.getOriginalFilename());
    	    System.out.println("File size: " + file.getSize());
    	    System.out.println("Authentication: " + authentication);
    	    System.out.println("Authenticated: " + authentication.isAuthenticated());
    	    System.out.println("Username: " + authentication.getName());

        String response =fileService.uploadFile(file,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }@GetMapping("/{id}")
    public ResponseEntity<FileResponse> getFile(
            @PathVariable("id") Long fileId,
            Authentication authentication) {

        FileResponse response =
                fileService.getFile(
                        fileId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(
            @PathVariable("id") Long fileId,
            Authentication authentication) {

        String response =
                fileService.deleteFile(
                        fileId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/trash")
    public ResponseEntity<List<TrashFileResponse>> getTrash(
            Authentication authentication) {

        List<TrashFileResponse> response =
                fileService.getTrash(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/restore")
    public ResponseEntity<String> restoreFile(
            @PathVariable("id") Long fileId,
            Authentication authentication) {

        String response =
                fileService.restoreFile(
                        fileId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<String> permanentlyDeleteFile(
            @PathVariable("id") Long fileId,
            Authentication authentication) {

        String response =
                fileService.permanentlyDeleteFile(
                        fileId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/move")
    public ResponseEntity<String> moveFile(@PathVariable Long id, @RequestBody MoveFileRequest request, Authentication authentication) {
        String response =fileService.moveFile(id,request.getFolderId(),authentication.getName() );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable Long fileId,Authentication authentication) {
        String email = authentication.getName();
        String signedUrl = fileService.downloadFile(fileId,email);

        return ResponseEntity.ok(signedUrl);
    }
    @PutMapping("/rename/{fileId}")
    public ResponseEntity<String> renameFile(@PathVariable Long fileId,@RequestParam String newFileName, Authentication authentication) {

        String email = authentication.getName();

        String response =fileService.renameFile(fileId,newFileName,email);

        return ResponseEntity.ok(response);
    }
}