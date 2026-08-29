package com.cloudstorage.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudstorage.entity.CloudFile;
import com.cloudstorage.entity.Folder;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.CloudFileRepository;
import com.cloudstorage.repository.FolderRepository;
import com.cloudstorage.repository.FolderShareRepository;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.service.FileService;
import com.cloudstorage.service.SupabaseStorageService;
import com.cloudstorage.dto.response.FileResponse;
import java.util.List;
import java.util.stream.Collectors;

import com.cloudstorage.dto.response.TrashFileResponse;
import com.cloudstorage.dto.response.FolderContentsResponse;
import com.cloudstorage.dto.response.FolderResponse;
import com.cloudstorage.entity.Share;
import com.cloudstorage.entity.SharePermission;
import com.cloudstorage.repository.ShareRepository;

@Service
public class FileServiceImpl implements FileService {
	
	private final FolderRepository folderRepository;
	private final UserRepository userRepository;
    private final CloudFileRepository cloudFileRepository;
    private final SupabaseStorageService storageService;
    private final ShareRepository shareRepository;
    private final FolderShareRepository folderShareRepository;

    public FileServiceImpl(
    		
            UserRepository userRepository,
            CloudFileRepository cloudFileRepository,
            SupabaseStorageService storageService,
            FolderRepository folderRepository,
            ShareRepository shareRepository,
            FolderShareRepository folderShareRepository) {

    	
        this.userRepository = userRepository;
        this.cloudFileRepository = cloudFileRepository;
        this.storageService = storageService;
        this.folderRepository = folderRepository;
        this.shareRepository = shareRepository;
        this.folderShareRepository = folderShareRepository;
    }

    @Override
    @Transactional
    public String uploadFile(MultipartFile file, String email) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required" );
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException( "User not found"));
        String originalFileName =file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new RuntimeException("Invalid file name");
        }
        String uniqueFileName =UUID.randomUUID()+ "-"+ originalFileName;
        String storagePath ="users/"+ user.getId()+ "/files/" + uniqueFileName;
        try {
            storageService.uploadFile( storagePath,file);
            CloudFile cloudFile =new CloudFile();
            cloudFile.setFileName(originalFileName);
            cloudFile.setOriginalFileName(originalFileName);
            cloudFile.setContentType(file.getContentType());
            cloudFile.setFileSize(file.getSize());
            cloudFile.setStoragePath( storagePath);
            cloudFile.setStorageProvider("SUPABASE");
            cloudFile.setOwner(user);

            cloudFileRepository.save(cloudFile);           
            return "File uploaded successfully";

        } catch (IOException e) {
            throw new RuntimeException( "Could not read uploaded file", e);
        }
    }
    @Override
    public FileResponse getFile(Long fileId, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));
       
        CloudFile cloudFile =cloudFileRepository.findById(fileId) .orElseThrow(() -> new RuntimeException("File not found" ));
        if (!canReadFile(cloudFile, user)) {
            throw new RuntimeException( "You do not have permission to access this file");
        }
        String signedUrl =storageService.createSignedUrl(cloudFile.getStoragePath(),600);
        return new FileResponse(
                cloudFile.getId(),
                cloudFile.getFileName(),
                cloudFile.getContentType(),
                cloudFile.getFileSize(),
                signedUrl
        );
    }
    @Override
    public String downloadFile(Long fileId, String email) {
        // 1. User find karo
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        // 2. File find karo
        CloudFile file =cloudFileRepository.findByIdAndDeletedAtIsNull(fileId).orElseThrow(() ->new RuntimeException("File not found"));
        // 3. Permission check Owner + Viewer + Editor download kar sakte hain
        if (!canReadFile(file, user)) {
            throw new RuntimeException("You do not have permission to download this file");
        }
        // 4. Supabase se temporary signed URL generate karo
        String signedUrl =storageService.createSignedUrl(file.getStoragePath(),600);
        return signedUrl; // 5. URL return karo
          }
    @Override
    @Transactional
    public String deleteFile(Long fileId,String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        CloudFile cloudFile =cloudFileRepository.findByIdAndDeletedAtIsNull(fileId).orElseThrow(() ->new RuntimeException("File not found"));

       if (!canEditFile(cloudFile, user)) {
            throw new RuntimeException("Editor permission required"
            );
        }
        cloudFile.setDeletedAt(LocalDateTime.now());

        cloudFileRepository.save(cloudFile);

        return "File moved to trash";
        }
    @Override
    public List<TrashFileResponse> getTrash(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException( "User not found"));

        List<CloudFile> files =cloudFileRepository.findByOwnerIdAndDeletedAtIsNotNull( user.getId());
                        
        return files.stream().map(file ->new TrashFileResponse(
                                file.getId(),
                                file.getFileName(),
                                file.getFileSize(),
                                file.getDeletedAt() )
                ) .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public String restoreFile(Long fileId,String email) {

         User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException( "User not found"));

        CloudFile cloudFile = cloudFileRepository .findById(fileId) .orElseThrow(() ->new RuntimeException("File not found" ));
 
        if (!cloudFile.getOwner().getId().equals(user.getId())) {

            throw new RuntimeException( "You do not have permission to restore this file");
        }

        if (cloudFile.getDeletedAt() == null) {
        	throw new RuntimeException( "File is not in trash");    
        }
        cloudFile.setDeletedAt(null);

        cloudFileRepository.save(cloudFile);

        return "File restored successfully";
    }

    @Override
    @Transactional
    public String permanentlyDeleteFile(Long fileId, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found" ));

        CloudFile cloudFile =cloudFileRepository .findById(fileId).orElseThrow(() -> new RuntimeException("File not found" ));

        if (!cloudFile.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to delete this file");
        }

        if (cloudFile.getDeletedAt() == null) {

            throw new RuntimeException("File must be moved to trash first");
        }

        storageService.deleteFile(cloudFile.getStoragePath());

        cloudFileRepository.delete(cloudFile);                
    
        return "File permanently deleted";
    }
    @Override
    @Transactional
    public String moveFile(Long fileId,Long folderId, String email) {

        User user = userRepository .findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        CloudFile file =cloudFileRepository .findByIdAndDeletedAtIsNull(fileId).orElseThrow(() ->new RuntimeException("File not found"));

        if (!canEditFile(file, user)) {
            throw new RuntimeException( "Editor permission required");
        }
                              
        if (folderId == null) {
        	file.setFolder(null);

        } else {
            Folder folder =folderRepository.findByIdAndDeletedAtIsNull(folderId).orElseThrow(() ->
                                    new RuntimeException("Destination folder not found"));
            if (!folder.getOwner().getId().equals(user.getId())) {
                throw new RuntimeException("You do not have permission to use this folder");
            }

            file.setFolder(folder);
        }
        cloudFileRepository.save(file);

        return "File moved successfully";
    }
    @Override
    @Transactional
    public String renameFile(Long fileId,String newFileName,String email) {
        // 1. User find karo
        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));
        // 2. File find karo
        CloudFile file =cloudFileRepository .findByIdAndDeletedAtIsNull(fileId).orElseThrow(() ->new RuntimeException("File not found"));
       // 3. File edit karne ki permission check karo
        if (!canEditFile(file, user)) {
            throw new RuntimeException("Editor permission required");
        }
        // 4. New file name validate karo
        if (newFileName == null || newFileName.isBlank()) {
            throw new RuntimeException( "New file name is required");
        }
        // 5. Old file name se extension nikalo
        String oldFileName = file.getFileName();
        String extension = "";
        int dotIndex = oldFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = oldFileName.substring(dotIndex);
        }
        // 6. Agar user extension nahi deta
        // to old extension automatically add karo
        if (!newFileName.contains(".") && !extension.isEmpty()) {
            newFileName = newFileName + extension;
        }
        // 7. Database mein new name save karo
        file.setFileName(newFileName);
        file.setOriginalFileName(newFileName);

        cloudFileRepository.save(file);

        return "File renamed successfully";
    }
    private SharePermission getFilePermission(CloudFile file, User user) {

        if (file.getOwner().getId().equals(user.getId())) {
            return null;
        }
        return shareRepository.findByFileIdAndSharedWithUserId(file.getId(), user.getId())
                .map(Share::getPermission).orElseThrow(() ->new RuntimeException("You do not have access to this file" ));
    }
    private boolean canReadFile(CloudFile file, User user) {

        if (file.getOwner().getId().equals(user.getId())) {
            return true;
        }
        //return
        	boolean directShare = shareRepository .findByFileIdAndSharedWithUserId(file.getId(),user.getId()).isPresent();
        	if(directShare) {
        		return true;
        	} Folder folder = file.getFolder();
            while (folder != null) {
                boolean folderShare =folderShareRepository.findByFolderIdAndSharedWithUserId( folder.getId(), user.getId()).isPresent();

                if (folderShare) {
                    return true; }

                folder = folder.getParent();
            }
            return false;
        }
    private boolean canEditFile(CloudFile file,User user) {
        if (file.getOwner().getId() .equals(user.getId())) {
            return true;
        } var directShare =shareRepository.findByFileIdAndSharedWithUserId(file.getId(),user.getId());
        if (directShare.isPresent()) {
        	return directShare.get().getPermission()== SharePermission.EDITOR;
        	}
        
        Folder folder = file.getFolder();
        while (folder != null) {
        	var folderShare =folderShareRepository.findByFolderIdAndSharedWithUserId(folder.getId(),user.getId());
        	if (folderShare.isPresent()) {
        		
        		return folderShare.get().getPermission()== SharePermission.EDITOR;
    }
        	folder = folder.getParent();
        	 }
              return false;
}
    private boolean isOwner(CloudFile file,User user) {

        return file.getOwner().getId().equals(user.getId());
    }
}