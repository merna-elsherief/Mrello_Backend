package com.example.mrellobackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskBulkUpdateDto implements Serializable {

    @NotNull(message = "Task ID is required")
    private Long id;

    private Long columnId;

    @Min(value = 0, message = "Position cannot be negative")
    private Integer position;


    private String status;

    private Long assignedUserId;
}
