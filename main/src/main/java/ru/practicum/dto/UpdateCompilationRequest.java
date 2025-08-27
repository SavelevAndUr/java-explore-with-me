package ru.practicum.dto;

import lombok.*;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @Length(min = 2, max = 50)
    private String title;

    public boolean getPinned() {
        return Objects.nonNull(pinned) && pinned;
    }
}