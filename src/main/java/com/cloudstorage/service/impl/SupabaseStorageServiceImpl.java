package com.cloudstorage.service.impl;

import java.io.IOException;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cloudstorage.config.SupabaseConfig;
import com.cloudstorage.service.SupabaseStorageService;
import com.cloudstorage.dto.response.SignedUrlResponse;

@Service
public class SupabaseStorageServiceImpl
        implements SupabaseStorageService {

    private final SupabaseConfig supabaseConfig;
    private final RestTemplate restTemplate;

    public SupabaseStorageServiceImpl(
            SupabaseConfig supabaseConfig) {

        this.supabaseConfig = supabaseConfig;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void uploadFile(
            String filePath,
            MultipartFile file) throws IOException {

        String url =
                supabaseConfig.getSupabaseUrl()
                + "/storage/v1/object/"
                + supabaseConfig.getBucket()
                + "/"
                + filePath;

        HttpHeaders headers = new HttpHeaders();
        
        headers.set(
        	    "apikey",
        	    supabaseConfig.getServiceRoleKey()
        	);

        headers.set(
                "Authorization",
                "Bearer "
                + supabaseConfig.getServiceRoleKey()
        );

        headers.setContentType(
                MediaType.parseMediaType(
                        file.getContentType()
                )
        );

        HttpEntity<byte[]> request =
                new HttpEntity<>(
                        file.getBytes(),
                        headers
                );

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {

            throw new RuntimeException(
                    "File upload failed: "
                    + response.getBody()
            );
        }
    }

    @Override
    public void deleteFile(String filePath) {

        String url =
                supabaseConfig.getSupabaseUrl()
                + "/storage/v1/object/"
                + supabaseConfig.getBucket()
                + "/"
                + filePath;

        HttpHeaders headers = new HttpHeaders();

        headers.set(
                "Authorization",
                "Bearer "
                + supabaseConfig.getServiceRoleKey()
        );

        HttpEntity<Void> request =
                new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.DELETE,
                        request,
                        String.class
                );

        if (!response.getStatusCode().is2xxSuccessful()) {

            throw new RuntimeException(
                    "File deletion failed: "
                    + response.getBody()
            );
        }
    }

    @Override
    public String createSignedUrl(
            String filePath,
            int expiresInSeconds) {

        String url =supabaseConfig.getSupabaseUrl()
                + "/storage/v1/object/sign/"
                + supabaseConfig.getBucket()
                + "/"
                + filePath;
               
        HttpHeaders headers = new HttpHeaders();

        headers.set( "Authorization","Bearer " + supabaseConfig.getServiceRoleKey());

        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"expiresIn\":" + expiresInSeconds+ "}";

        HttpEntity<String> request =new HttpEntity<>(body, headers);

        ResponseEntity<SignedUrlResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, SignedUrlResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {

            throw new RuntimeException("Could not create signed URL");
        }
        
        String signedPath = response.getBody().getSignedURL();

        if (signedPath.startsWith("http")) {
            return signedPath;
        }
        return supabaseConfig.getSupabaseUrl()
                + "/storage/v1"
                + signedPath;
    }
    }
