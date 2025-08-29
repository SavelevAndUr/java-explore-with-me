package ru.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.model.State;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto extends EventShortDto {
    private String createdOn;
    private String description;
    @NotNull
    private LocationDto location;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private State state;

    @Builder(builderMethodName = "childBuilder")
    public EventFullDto(@NotNull String annotation, @NotNull CategoryDto category, Integer confirmedRequests,
                        @NotNull String eventDate, Long id, @NotNull UserShortDto initiator, @NotNull Boolean paid,
                        @NotNull String title, Integer views, Integer likes, Integer dislikes, String createdOn,
                        String description, LocationDto location, Integer participantLimit, String publishedOn,
                        Boolean requestModeration, State state) {
        super(annotation, category, confirmedRequests, eventDate, id, initiator, paid, title, views, likes, dislikes);
        this.createdOn = createdOn;
        this.description = description;
        this.location = location;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
    }

    public Integer getParticipantLimit() {
        return Objects.isNull(participantLimit) ? 0 : participantLimit;
    }

    public Boolean getRequestModeration() {
        return Objects.isNull(requestModeration) || requestModeration;
    }

}