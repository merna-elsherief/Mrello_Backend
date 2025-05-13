package com.example.mrellobackend.auth;

import com.example.mrellobackend.auth.validation.PasswordsMatch;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordsMatch
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    private String firstName;

    private String lastName;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "password is required")
    @Transient
    private String confirmPassword;

    @Email(message = "Email is not valid")
    @NotBlank(message = "Email is required")
    private String email;
}
