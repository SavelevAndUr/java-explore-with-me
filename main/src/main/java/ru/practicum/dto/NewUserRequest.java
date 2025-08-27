package ru.practicum.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserRequest {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 250, message = "Name must be between 2 and 250 characters")
    @Pattern(regexp = "\\S.*\\S", message = "Name cannot consist only of whitespace")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters")
    @Pattern(regexp = "\\S+@\\S+\\.\\S+", message = "Email should be valid")
    private String email;
}