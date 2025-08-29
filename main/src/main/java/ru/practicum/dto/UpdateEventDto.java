package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class UpdateEventDto extends NewEventDto {
    private final StateAction stateAction;

    @Builder(builderMethodName = "childBuilder")
    public UpdateEventDto(@Length(min = 20, max = 2000) String annotation, Long category,
                          @Length(min = 20, max = 7000) String description, String eventDate, LocationDto location,
                          Boolean paid, Integer participantLimit, Boolean requestModeration, String title,
                          StateAction stateAction) {
        super(annotation, category, description, eventDate, location, paid, participantLimit, requestModeration, title);
        this.stateAction = stateAction;
    }

    @Override
    public Boolean getPaid() {
        return super.paid;
    }

    @Override
    public Integer getParticipantLimit() {
        return super.participantLimit;
    }

    @Override
    public Boolean getRequestModeration() {
        return super.requestModeration;
    }

}
