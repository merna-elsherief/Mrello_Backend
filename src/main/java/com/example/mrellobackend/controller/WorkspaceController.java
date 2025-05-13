package com.example.mrellobackend.controller;

import com.example.mrellobackend.entity.Workspace;
import com.example.mrellobackend.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<Workspace> createWorkspace(@RequestBody Workspace workspace) {
        Workspace createdWorkspace = workspaceService.createWorkspace(workspace);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWorkspace);
    }

    @GetMapping("/my-workspaces")
    public ResponseEntity<List<Workspace>> getMyWorkspaces() {
        List<Workspace> workspaces = workspaceService.getCurrentUserWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<Workspace> getWorkspace(@PathVariable Long workspaceId) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspace);
    }

    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<Workspace> addMember(
            @PathVariable Long workspaceId,
            @RequestParam Long userId
    ) {
        Workspace updatedWorkspace = workspaceService.addMember(workspaceId, userId);
        return ResponseEntity.ok(updatedWorkspace);
    }
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
