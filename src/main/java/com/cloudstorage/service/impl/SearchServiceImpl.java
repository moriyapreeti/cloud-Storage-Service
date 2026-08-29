package com.cloudstorage.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cloudstorage.dto.response.SearchResultResponse;
import com.cloudstorage.entity.CloudFile;
import com.cloudstorage.entity.Folder;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.CloudFileRepository;
import com.cloudstorage.repository.FolderRepository;
import com.cloudstorage.repository.FolderShareRepository;
import com.cloudstorage.repository.ShareRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.SearchService;

@Service
public class SearchServiceImpl
        implements SearchService {

    private final CloudFileRepository cloudFileRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final ShareRepository shareRepository;
    private final FolderShareRepository folderShareRepository;

    public SearchServiceImpl(
            CloudFileRepository cloudFileRepository,
            FolderRepository folderRepository,
            UserRepository userRepository,
            ShareRepository shareRepository,
            FolderShareRepository folderShareRepository) {

        this.cloudFileRepository =cloudFileRepository;
        this.folderRepository =folderRepository;
        this.userRepository =userRepository;
        this.folderShareRepository = folderShareRepository;
        this.shareRepository = shareRepository;
    }
    @Override
    public Page<SearchResultResponse> search(
            String keyword,
            String type,
            String contentType,
            String email,
            Pageable pageable) {

        User user =userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found" ));
        if (keyword == null|| keyword.isBlank()) {
            throw new RuntimeException( "Search keyword is required");
        }
        if ("FILE".equalsIgnoreCase(type)) {
            return searchFiles(
                    keyword,
                    contentType,
                    user,
                    pageable
            );
        }
        if ("FOLDER".equalsIgnoreCase(type)) {

            return searchFolders(
                    keyword,
                    user,
                    pageable
            );
        }
        return searchAll(
                keyword,
                contentType,
                user,
                pageable
        );
    }
   private Page<SearchResultResponse> searchFiles(
            String keyword,
            String contentType,
            User user,
            Pageable pageable) {

        List<CloudFile> allFiles =cloudFileRepository.searchAllMatchingFiles(keyword,contentType);

        List<SearchResultResponse> results =allFiles.stream().map(file -> {
        	String permission =getFilePermission(file, user);
        	if (permission == null) {
                 return null;
              }
            return convertFile(file,permission);
                        })
               .filter(result ->result != null).toList();

        return createPage(results, pageable );
    }
    private Page<SearchResultResponse> searchFolders( String keyword,User user, Pageable pageable) {

                List<Folder> folders = folderRepository.searchAllMatchingFolders(keyword);

                List<SearchResultResponse> results =folders.stream().map(folder -> {
                	String permission =getFolderPermission(folder,user);
                		if (permission == null) {
                			return null;
                		}
                		return convertFolder(folder,permission);
                                })
                		.filter(result ->result != null).toList();

                return createPage(results,pageable);
            }
    private void sortResults(
            List<SearchResultResponse> results,
            Pageable pageable) {

        boolean descending =pageable.getSort().stream().findFirst().map(order ->order.isDescending()).orElse(true);

        results.sort(java.util.Comparator.comparing(SearchResultResponse::getName,String.CASE_INSENSITIVE_ORDER));
 
        if (descending) {
            java.util.Collections.reverse(
                    results
            );
        }}
    private SearchResultResponse convertFile(CloudFile file, String permission) {

        Long parentFolderId = null;

        if (file.getFolder() != null) {
            parentFolderId =file.getFolder().getId();
        }

        return new SearchResultResponse(
                file.getId(),
                file.getFileName(),
                "FILE",
                file.getContentType(),
                parentFolderId,
                permission, file.getFileSize()
        );
    }

    private SearchResultResponse convertFolder(Folder folder,String permission) {

        Long parentFolderId = null;

        if (folder.getParent() != null) { parentFolderId =folder.getParent().getId();
        }

        return new SearchResultResponse(folder.getId(),folder.getName(),"FOLDER",null,parentFolderId,permission,null);
    }
    private String getFilePermission( CloudFile file,User user) {

        if (file.getOwner().getId().equals(user.getId())) {
            return "OWNER";
        }

        var directShare =shareRepository.findByFileIdAndSharedWithUserId(file.getId(),user.getId());
        if (directShare.isPresent()) {
            return directShare.get().getPermission().name();
        }

        var folder = file.getFolder();

        while (folder != null) {

            var folderShare = folderShareRepository.findByFolderIdAndSharedWithUserId(folder.getId(),user.getId());

            if (folderShare.isPresent()) {

                return folderShare.get().getPermission() .name();
            }

            folder = folder.getParent();
        }

        return null;
    }
    private Page<SearchResultResponse> createPage(List<SearchResultResponse> results,
            Pageable pageable) {

        int start =(int) pageable.getOffset(); 
        if (start >= results.size()) {
        	return new org.springframework.data.domain.PageImpl<>(
                            List.of(),
                            pageable,results.size()
                    );
                }
        int end =Math.min(start + pageable.getPageSize(),results.size() );  
        List<SearchResultResponse> pageContent =results.subList(start, end);
       
        return new org.springframework.data.domain.PageImpl<>(
                results.subList(start, end),
                pageable,results.size()
        );
    }
    private String getFolderPermission(Folder folder, User user) {

        if (folder.getOwner().getId().equals(user.getId())) {
            return "OWNER";
        }
        var share =folderShareRepository.findByFolderIdAndSharedWithUserId(folder.getId(), user.getId() );

        if (share.isPresent()) {
            return share.get().getPermission().name();
        }
        Folder current =folder.getParent();
        while (current != null) {
            var parentShare = folderShareRepository.findByFolderIdAndSharedWithUserId(current.getId(),user.getId());
            if (parentShare.isPresent()) {
                return parentShare.get().getPermission().name();
            }
            current = current.getParent();
        }
        return null;
    }
    private Page<SearchResultResponse> searchAll( String keyword,String ContentType,User user,Pageable pageable) {

        List<SearchResultResponse> results =new java.util.ArrayList<>();

        List<CloudFile> files =cloudFileRepository.searchAllMatchingFiles(keyword,ContentType);

        for (CloudFile file : files) {
            String permission =getFilePermission(file,user);
            if (permission != null) {
                results.add( convertFile(file,permission));
            } }
        List<Folder> folders =folderRepository.searchAllMatchingFolders(keyword);

        for (Folder folder : folders) {
            String permission =getFolderPermission(folder,user);
            if (permission != null) {
                results.add( convertFolder(folder, permission ));
            }
        }java.util.Comparator<SearchResultResponse> comparator =
                java.util.Comparator.comparing(
                        SearchResultResponse::getName,
                        String.CASE_INSENSITIVE_ORDER
                );
        sortResults(
                results,
                pageable
        );

        return createPage(
                results,
                pageable
        );
    }
}