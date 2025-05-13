package com.example.mrellobackend.dto;

import com.example.mrellobackend.dao.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Integer position;
    private TaskStatus status;
    private LocalDate dueDate;
    private Long columnId;
    private Long assignedUserId;
}
