package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.dto.LocationDto;
import ru.practicum.validation.Marker;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    @NotNull(groups = Marker.OnCreate.class)
    @Length(min = 20, max = 2000)
    private String annotation;
    @NotNull(groups = Marker.OnCreate.class)
    private Long category;
    @NotNull(groups = Marker.OnCreate.class)
    @Length(min = 20, max = 7000)
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private String eventDate;
    @NotNull(groups = Marker.OnCreate.class)
    private LocationDto location;
    protected Boolean paid;
    protected Integer participantLimit;
    protected Boolean requestModeration;
    @NotNull(groups = Marker.OnCreate.class)
    @Length(min = 3, max = 120)
    private String title;

    public Boolean getPaid() {
        return !Objects.isNull(paid) && paid;
    }

    public Integer getParticipantLimit() {
        return Objects.isNull(participantLimit) ? 0 : participantLimit;
    }

    public Boolean getRequestModeration() {
        return Objects.isNull(requestModeration) || requestModeration;
    }
}