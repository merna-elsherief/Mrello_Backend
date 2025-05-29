package com.example.mrellobackend.service;

import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.auth.user.UserRepository;
import com.example.mrellobackend.dto.WorkspaceCreateDto;
import com.example.mrellobackend.dto.WorkspaceDto;
import com.example.mrellobackend.entity.Workspace;
import com.example.mrellobackend.exception.ResourceNotFoundException;
import com.example.mrellobackend.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService{
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }
    public static WorkspaceDto convertToDto(Workspace workspace) {
        if (workspace == null) return null;

        return WorkspaceDto.builder()
                .id(workspace.getId())
                .title(workspace.getTitle())
                .description(workspace.getDescription())
                .ownerId(workspace.getOwner() != null ? workspace.getOwner().getId() : null)
                .memberIds(workspace.getMembers() != null
                        ? workspace.getMembers().stream()
                        .map(user -> user.getId())
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
    public WorkspaceDto createWorkspace(WorkspaceCreateDto workspaceCreateDto) {
        Workspace savedWorkspace = new Workspace();
        savedWorkspace.setOwner(getCurrentUser());
        savedWorkspace.setTitle(workspaceCreateDto.getTitle());
        savedWorkspace.setDescription(workspaceCreateDto.getDescription());
        workspaceRepository.save(savedWorkspace);
        return convertToDto(savedWorkspace);
    }

    public Workspace addMember(Long workspaceId, Long userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        if (!workspace.getOwner().equals(getCurrentUser())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can add members");
        }

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        workspace.getMembers().add(newMember);
        return workspaceRepository.save(workspace);
    }
    public Workspace removeMemberFromWorkspace(Long workspaceId, Long userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        if (!workspace.getOwner().equals(getCurrentUser())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can add members");
        }
        workspace.getMembers().removeIf(member -> member.getId().equals(userId));
        return workspaceRepository.save(workspace);
    }

    public WorkspaceDto updateWorkspace(Long workspaceId, WorkspaceDto updatedWorkspace) {
        User currentUser = getCurrentUser();
        Workspace existingWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        if (!existingWorkspace.getOwner().equals(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this workspace");
        }

        existingWorkspace.setTitle(updatedWorkspace.getTitle());
        existingWorkspace.setDescription(updatedWorkspace.getDescription());

        Workspace savedWorkspace = workspaceRepository.save(existingWorkspace);

        return convertToDto(savedWorkspace);
    }

    public List<Workspace> getCurrentUserWorkspaces() {
        User currentUser = getCurrentUser();
        return workspaceRepository.findByOwnerOrMembersContaining(currentUser, currentUser);
    }

    public Workspace getWorkspaceById(Long workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workspace not found with id: " + workspaceId
                ));
    }

    public void deleteWorkspace(Long workspaceId) {
        User currentUser = getCurrentUser();
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        if (!workspace.getOwner().equals(currentUser)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the workspace owner can delete this workspace"
            );
        }

        workspaceRepository.delete(workspace);
    }
}

