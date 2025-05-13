package com.example.mrellobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnUpdateDto {
    private Long id; // Required for bulk updates
    private String title;
    private Integer position;
}
