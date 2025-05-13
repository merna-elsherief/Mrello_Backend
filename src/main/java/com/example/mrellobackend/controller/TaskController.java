package com.example.mrellobackend.controller;

import com.example.mrellobackend.dto.TaskBulkUpdateDto;
import com.example.mrellobackend.dto.TaskCreateDto;
import com.example.mrellobackend.dto.TaskDto;
import com.example.mrellobackend.dto.TaskUpdateDto;
import com.example.mrellobackend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long taskId) {
        TaskDto task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<TaskDto>> getTasksByColumn(@PathVariable Long columnId) {
        List<TaskDto> tasks = taskService.getTasksByColumnId(columnId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateDto taskDto
    ) {
        TaskDto updatedTask = taskService.updateTask(taskId, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskDto> moveTask(
            @PathVariable Long taskId,
            @RequestParam Long newColumnId,
            @RequestParam(required = false) Integer newPosition
    ) {
        TaskDto movedTask = taskService.moveTask(taskId, newColumnId, newPosition);
        return ResponseEntity.ok(movedTask);
    }

    @PatchMapping("/{taskId}/reorder")
    public ResponseEntity<Void> reorderTask(
            @PathVariable Long taskId,
            @RequestParam Integer newPosition
    ) {
        taskService.reorderTask(taskId, newPosition);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{taskId}/assignee")
    public ResponseEntity<TaskDto> changeAssignee(
            @PathVariable Long taskId,
            @RequestParam Long userId
    ) {
        TaskDto updatedTask = taskService.changeAssignee(taskId, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateStatus(
            @PathVariable Long taskId,
            @RequestParam String status
    ) {
        TaskDto updatedTask = taskService.updateStatus(taskId, status);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<TaskDto>> bulkUpdateTasks(
            @Valid @RequestBody List<TaskBulkUpdateDto> taskDtos
    ) {
        List<TaskDto> updatedTasks = taskService.bulkUpdateTasks(taskDtos);
        return ResponseEntity.ok(updatedTasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDto>> getTasksByAssignee(@PathVariable Long userId) {
        List<TaskDto> tasks = taskService.getTasksByAssignee(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/due-soon")
    public ResponseEntity<List<TaskDto>> getDueSoonTasks() {
        List<TaskDto> tasks = taskService.getDueSoonTasks();
        return ResponseEntity.ok(tasks);
    }
}
