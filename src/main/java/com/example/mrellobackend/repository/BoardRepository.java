package com.example.mrellobackend.repository;

import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByWorkspaceId(Long workspaceId);
    Optional<Board> findByIdAndWorkspaceId(Long boardId, Long workspaceId);

    @Query("SELECT b FROM Board b WHERE b.workspace.owner = :user OR :user MEMBER OF b.workspace.members")
    List<Board> findByWorkspaceOwnerOrWorkspaceMembersContaining(
            @Param("user") User user
    );}
