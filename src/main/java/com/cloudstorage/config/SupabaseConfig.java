package com.cloudstorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.bucket}")
    private String bucket;

    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    public String getServiceRoleKey() {
        return serviceRoleKey;
    }

    public String getBucket() {
        return bucket;
    }
}