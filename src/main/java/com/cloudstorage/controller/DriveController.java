package com.cloudstorage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.cloudstorage.dto.response.DriveResponse;
import com.cloudstorage.service.DriveService;

@RestController
@RequestMapping("/api/drive")
public class DriveController {

    private final DriveService driveService;

    public DriveController(
            DriveService driveService) {

        this.driveService = driveService;
    }

    @GetMapping
    public ResponseEntity<DriveResponse> getMyDrive(
            Authentication authentication) {

        DriveResponse response =
                driveService.getMyDrive(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}