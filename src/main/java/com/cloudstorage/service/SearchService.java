
package com.cloudstorage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cloudstorage.dto.response.SearchResultResponse;

public interface SearchService {

    Page<SearchResultResponse> search(
            String keyword,
            String type,
            String ContentType,
            String email,
            Pageable pageable);
}