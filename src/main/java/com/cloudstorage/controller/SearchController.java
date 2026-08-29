package com.cloudstorage.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudstorage.dto.response.SearchResultResponse;
import com.cloudstorage.service.SearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(
            SearchService searchService) {

        this.searchService =
                searchService;
    }

    @GetMapping
    public ResponseEntity<Page<SearchResultResponse>>search(

            @RequestParam String keyword,

            @RequestParam(
                    defaultValue = "ALL"
            )
            String type, 
            @RequestParam(
                    required = false
            )
            String contentType,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size,
            @RequestParam(
                    defaultValue = "name"
            )
            String sortBy,

            @RequestParam(
                    defaultValue = "ASC"
            )
            String direction,

            Authentication authentication)
        {
    	if (size < 1) {
            size = 10;
        }

        if (size > 50) {
            size = 50;
        }

        if (page < 0) {
            page = 0;
        }
        if (!sortBy.equals("id")&& !sortBy.equals("name")&& !sortBy.equals("fileName")) {

            sortBy = "id";
        }
        Sort.Direction sortDirection;
        try {
            sortDirection =Sort.Direction.valueOf(direction.toUpperCase());

        } catch (IllegalArgumentException e) {
            sortDirection = Sort.Direction.ASC;
        }
        Pageable pageable =PageRequest.of(page,size,Sort.by(sortDirection,sortBy));
  
        Page<SearchResultResponse> result =
                searchService.search(
                        keyword,
                        type,
                        contentType,
                        authentication.getName(),
                        pageable
                );

        return ResponseEntity.ok(result);
    }
}