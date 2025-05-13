package com.example.mrellobackend.dto;

import com.example.mrellobackend.dao.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDto {
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private Long columnId;
    private Long assignedUserId;
}
