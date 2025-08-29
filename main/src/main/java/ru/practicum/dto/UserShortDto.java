package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    private Long id;
    @NotNull
    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
}