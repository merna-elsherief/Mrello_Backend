package com.example.mrellobackend.repository;

import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByOwnerId(Long ownerId);
    Optional<Workspace> findById(Long id);
    Optional<Workspace> findByTitle(String title);
    List<Workspace> findByOwnerOrMembersContaining(User owner, User member);

    @Query("SELECT COUNT(w) > 0 FROM Workspace w " +
            "WHERE :user1 MEMBER OF w.members " +
            "AND :user2 MEMBER OF w.members")
    boolean existsByMembersContainingAndMembersContaining(
            @Param("user1") User user1,
            @Param("user2") User user2
    );}
