package com.example.mrellobackend.dto;

import com.example.mrellobackend.auth.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceCreateDto {
    private String title;
    private String description;
}
