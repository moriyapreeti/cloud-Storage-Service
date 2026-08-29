package com.cloudstorage.service;

import com.cloudstorage.dto.response.DriveResponse;

public interface DriveService {

    DriveResponse getMyDrive(
            String email);
}