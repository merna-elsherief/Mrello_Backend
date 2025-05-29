package com.example.mrellobackend.service;

import com.example.mrellobackend.dto.TaskBulkUpdateDto;
import com.example.mrellobackend.dto.TaskCreateDto;
import com.example.mrellobackend.dto.TaskDto;
import com.example.mrellobackend.dto.TaskUpdateDto;

import java.util.List;

public interface TaskService {
    TaskDto createTask(TaskCreateDto taskDto);

    TaskDto getTaskById(Long taskId);

    List<TaskDto> getTasksByColumnId(Long columnId);

    TaskDto updateTask(Long taskId, TaskUpdateDto taskDto);

    void deleteTask(Long taskId);

    TaskDto moveTask(Long taskId, Long newColumnId, Integer newPosition);

    void reorderTask(Long taskId, Integer newPosition);

    TaskDto changeAssignee(Long taskId, Long userId);

    TaskDto updateStatus(Long taskId, String status);

    List<TaskDto> bulkUpdateTasks(List<TaskBulkUpdateDto> taskDtos);

    List<TaskDto> getTasksByAssignee(Long userId);

    List<TaskDto> getDueSoonTasks();
}
