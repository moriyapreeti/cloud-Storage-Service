package com.cloudstorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cloudstorage.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;


public interface FolderRepository
        extends JpaRepository<Folder, Long> {

    List<Folder> findByOwnerIdAndParentIdAndDeletedAtIsNull(
            Long ownerId,
            Long parentId
    );

    List<Folder> findByOwnerIdAndParentIsNullAndDeletedAtIsNull(
            Long ownerId
    );

    Optional<Folder> findByIdAndDeletedAtIsNull(
            Long id
    );

    boolean existsByOwnerIdAndParentIdAndNameIgnoreCaseAndDeletedAtIsNull(  Long ownerId, Long parentId,String name );
   
    List<Folder> findByParentIdAndDeletedAtIsNull( Long parentId);
    @Query("""
    	    SELECT f
    	    FROM Folder f
    	    WHERE f.deletedAt IS NULL
    	    AND f.owner.id = :userId
    	    AND LOWER(f.name)
    	        LIKE LOWER(CONCAT('%', :keyword, '%'))
    	""")
    	Page<Folder> searchOwnedFolders(
    	        @Param("userId") Long userId,
    	        @Param("keyword") String keyword,
    	        Pageable pageable);
    
    @Query("""
    	    SELECT f
    	    FROM Folder f
    	    WHERE f.deletedAt IS NULL
    	    AND LOWER(f.name)
    	        LIKE LOWER(CONCAT('%', :keyword, '%'))
    	""")
    	List<Folder> searchAllMatchingFolders(
    	        @Param("keyword") String keyword);
           
}