package ru.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import ru.practicum.validation.Marker;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    private Set<Integer> events;
    private Boolean pinned;
    @NotNull(groups = Marker.OnCreate.class)
    @NotBlank
    @Length(min = 2, max = 50)
    private String title;

    public Boolean getPinned() {
        return !Objects.isNull(pinned) && pinned;
    }
}