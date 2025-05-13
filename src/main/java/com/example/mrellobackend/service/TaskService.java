package com.example.mrellobackend.service;

import com.example.mrellobackend.auth.user.User;
import com.example.mrellobackend.auth.user.UserRepository;
import com.example.mrellobackend.dao.TaskStatus;
import com.example.mrellobackend.dto.TaskBulkUpdateDto;
import com.example.mrellobackend.dto.TaskCreateDto;
import com.example.mrellobackend.dto.TaskDto;
import com.example.mrellobackend.dto.TaskUpdateDto;
import com.example.mrellobackend.entity.Column;
import com.example.mrellobackend.entity.Task;
import com.example.mrellobackend.exception.AccessDeniedException;
import com.example.mrellobackend.exception.ResourceNotFoundException;
import com.example.mrellobackend.repository.ColumnRepository;
import com.example.mrellobackend.repository.TaskRepository;
import com.example.mrellobackend.repository.WorkspaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @Transactional
    public TaskDto createTask(TaskCreateDto taskDto) {
        User currentUser = getCurrentUser();
        Column column = columnRepository.findById(taskDto.getColumnId())
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        // Verify user has access to the board
        if (!column.getBoard().getWorkspace().getMembers().contains(currentUser)) {
            throw new AccessDeniedException("No permission to create tasks in this column");
        }

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setDueDate(taskDto.getDueDate());
        task.setPosition(taskRepository.countByColumnId(column.getId())); // Add to end
        task.setColumn(column);

        if (taskDto.getAssignedUserId() != null) {
            User assignee = userRepository.findById(taskDto.getAssignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignedUser(assignee);
        }

        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }

    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        verifyTaskAccess(task);
        return convertToDto(task);
    }

    public List<TaskDto> getTasksByColumnId(Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        verifyColumnAccess(column);
        return taskRepository.findByColumnIdOrderByPositionAsc(columnId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public TaskDto updateTask(Long taskId, TaskUpdateDto taskDto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        verifyTaskAccess(task);

        if (taskDto.getTitle() != null) task.setTitle(taskDto.getTitle());
        if (taskDto.getDescription() != null) task.setDescription(taskDto.getDescription());
        if (taskDto.getStatus() != null) task.setStatus(taskDto.getStatus());
        if (taskDto.getDueDate() != null) task.setDueDate(taskDto.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        verifyTaskDeletionPermission(task);
        taskRepository.delete(task);

        taskRepository.decrementPositions(task.getColumn().getId(), task.getPosition());
    }
    @Transactional
    public TaskDto moveTask(Long taskId, Long newColumnId, Integer newPosition) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        Column newColumn = columnRepository.findById(newColumnId)
                .orElseThrow(() -> new ResourceNotFoundException("New column not found"));

        verifyTaskAccess(task);
        verifyColumnAccess(newColumn);

        if (newPosition == null) {
            newPosition = taskRepository.countByColumnId(newColumnId);
        }

        taskRepository.decrementPositions(task.getColumn().getId(), task.getPosition());

        taskRepository.incrementPositions(newColumnId, newPosition);

        task.setColumn(newColumn);
        task.setPosition(newPosition);

        Task movedTask = taskRepository.save(task);
        return convertToDto(movedTask);
    }
    @Transactional
    public void reorderTask(Long taskId, Integer newPosition) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        verifyTaskAccess(task);

        if (task.getPosition() < newPosition) {
            taskRepository.decrementPositionsInRange(
                    task.getColumn().getId(),
                    task.getPosition() + 1,
                    newPosition
            );
        } else {
            taskRepository.incrementPositionsInRange(
                    task.getColumn().getId(),
                    newPosition,
                    task.getPosition() - 1
            );
        }

        task.setPosition(newPosition);
        taskRepository.save(task);
    }
    @Transactional
    public TaskDto changeAssignee(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        verifyTaskAccess(task);

        if (!task.getColumn().getBoard().getWorkspace().getMembers().contains(assignee)) {
            throw new AccessDeniedException("Assignee must be a workspace member");
        }

        task.setAssignedUser(assignee);
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }
    @Transactional
    public TaskDto updateStatus(Long taskId, String status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        verifyTaskAccess(task);
        task.setStatus(TaskStatus.valueOf(status.toUpperCase()));

        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }

    @Transactional
    public List<TaskDto> bulkUpdateTasks(List<TaskBulkUpdateDto> taskDtos) {
        return taskDtos.stream()
                .map(dto -> {
                    Task task = taskRepository.findById(dto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + dto.getId()));

                    verifyTaskAccess(task);

                    if (dto.getColumnId() != null) {
                        Column newColumn = columnRepository.findById(dto.getColumnId())
                                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));
                        verifyColumnAccess(newColumn);
                        task.setColumn(newColumn);
                    }

                    if (dto.getPosition() != null) task.setPosition(dto.getPosition());
                    if (dto.getStatus() != null) task.setStatus(TaskStatus.valueOf(dto.getStatus().toUpperCase()));
                    if (dto.getAssignedUserId() != null) {
                        User assignee = userRepository.findById(dto.getAssignedUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                        task.setAssignedUser(assignee);
                    }

                    return taskRepository.save(task);
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private void verifyTaskAccess(Task task) {
        User currentUser = getCurrentUser();
        if (!task.getColumn().getBoard().getWorkspace().getMembers().contains(currentUser)) {
            throw new AccessDeniedException("No permission to access this task");
        }
    }

    private void verifyColumnAccess(Column column) {
        User currentUser = getCurrentUser();
        if (!column.getBoard().getWorkspace().getMembers().contains(currentUser)) {
            throw new AccessDeniedException("No permission to access this column");
        }
    }

    private void verifyTaskDeletionPermission(Task task) {
        User currentUser = getCurrentUser();
        boolean isOwner = task.getColumn().getBoard().getWorkspace().getOwner().equals(currentUser);
        boolean isAssignee = task.getAssignedUser() != null &&
                task.getAssignedUser().equals(currentUser);

        if (!isOwner && !isAssignee) {
            throw new AccessDeniedException("Only owner or assignee can delete this task");
        }
    }

    private TaskDto convertToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .position(task.getPosition())
                .columnId(task.getColumn().getId())
                .assignedUserId(task.getAssignedUser() != null ? task.getAssignedUser().getId() : null)
                .build();
    }

    public List<TaskDto> getTasksByAssignee(Long userId) {
        User currentUser = getCurrentUser();
        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify requested user is in at least one shared workspace with current user
        if (!workspaceRepository.existsByMembersContainingAndMembersContaining(currentUser, assignee)) {
            throw new AccessDeniedException("Cannot view tasks of this user");
        }

        return taskRepository.findByAssignedUserOrderByDueDateAsc(assignee)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public List<TaskDto> getDueSoonTasks() {
        User currentUser = getCurrentUser();
        LocalDate threshold = LocalDate.now().plusDays(3); // Tasks due in next 3 days

        return taskRepository.findByAssignedUserAndDueDateBetweenOrderByDueDateAsc(
                        currentUser,
                        LocalDate.now(),
                        threshold
                )
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
