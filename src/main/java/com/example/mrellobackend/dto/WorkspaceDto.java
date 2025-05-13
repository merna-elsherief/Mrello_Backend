package com.example.mrellobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDto {
    private Long id;
    private String title;
    private String description;
    private Long ownerId;
    private List<Long> memberIds;
    private LocalDateTime createdAt;
}
