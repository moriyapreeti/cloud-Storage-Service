package com.cloudstorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudstorage.entity.CloudFile;
import com.cloudstorage.entity.Folder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CloudFileRepository extends JpaRepository<CloudFile, Long> {

    List<CloudFile> findByOwnerId(Long ownerId);
    
    List<CloudFile> findByOwnerIdAndDeletedAtIsNotNull(Long ownerId);

    Optional<CloudFile> findByIdAndDeletedAtIsNull(Long id);
    
    List<CloudFile> findByOwnerIdAndFolderIdAndDeletedAtIsNull(Long ownerId,Long folderId);
    
    List<CloudFile> findByOwnerIdAndFolderIsNullAndDeletedAtIsNull(
            Long ownerId
    );
    
   /* @Query("""
    	    SELECT f
    	    FROM CloudFile f
    	    WHERE f.deletedAt IS NULL
    	    AND LOWER(f.fileName)
    	        LIKE LOWER(CONCAT('%', :keyword, '%'))
    	""")
    	Page<CloudFile> searchFiles(
    	        @Param("keyword") String keyword,
    	        Pageable pageable);
}*/
@Query("""
	    SELECT f
	    FROM CloudFile f
	    WHERE f.deletedAt IS NULL
	    AND f.owner.id = :userId
	    AND LOWER(f.fileName)
	        LIKE LOWER(CONCAT('%', :keyword, '%'))
	""")
	Page<CloudFile> searchOwnedFiles(
	        @Param("userId") Long userId,
	        @Param("keyword") String keyword,
	        Pageable pageable);
@Query("""
	    SELECT DISTINCT f
	    FROM CloudFile f
	    LEFT JOIN Share s
	        ON s.file.id = f.id
	    WHERE f.deletedAt IS NULL
	    AND LOWER(f.fileName)
	        LIKE LOWER(CONCAT('%', :keyword, '%'))
	    AND (
	        s.sharedWithUser.id = :userId
	    )
	""")
	Page<CloudFile> searchSharedFiles(
	        @Param("keyword") String keyword,
	        @Param("userId") Long userId,
	        Pageable pageable);

@Query("""
	    SELECT DISTINCT f
	    FROM CloudFile f
	    JOIN FolderShare fs
	        ON fs.folder.id = f.folder.id
	    WHERE f.deletedAt IS NULL
	    AND fs.sharedWithUser.id = :userId
	    AND LOWER(f.fileName)
	        LIKE LOWER(CONCAT('%', :keyword, '%'))
	""")
	Page<CloudFile> searchFolderSharedFiles(
	        @Param("keyword") String keyword,
	        @Param("userId") Long userId,
	        Pageable pageable);
@Query("""
	    SELECT f
	    FROM CloudFile f
	    WHERE f.deletedAt IS NULL
	    AND LOWER(f.fileName)
	        LIKE LOWER(CONCAT('%', :keyword, '%'))
	    AND (
	        :contentType IS NULL
	        OR f.contentType = :contentType
	    )
	""")
	List<CloudFile> searchAllMatchingFiles(
	        @Param("keyword") String keyword,
	        @Param("contentType") String contentType);
}