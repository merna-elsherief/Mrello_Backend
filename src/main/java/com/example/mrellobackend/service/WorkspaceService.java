package com.example.mrellobackend.service;


import com.example.mrellobackend.dto.WorkspaceCreateDto;
import com.example.mrellobackend.dto.WorkspaceDto;
import com.example.mrellobackend.entity.Workspace;

import java.util.List;

public interface WorkspaceService {
    WorkspaceDto createWorkspace(WorkspaceCreateDto workspaceCreateDto);
    Workspace addMember(Long workspaceId, Long userId);
    Workspace removeMemberFromWorkspace(Long workspaceId, Long userId);
    WorkspaceDto updateWorkspace(Long workspaceId, WorkspaceDto updatedWorkspace);
    List<Workspace> getCurrentUserWorkspaces();
    Workspace getWorkspaceById(Long workspaceId);
    void deleteWorkspace(Long workspaceId);
}
