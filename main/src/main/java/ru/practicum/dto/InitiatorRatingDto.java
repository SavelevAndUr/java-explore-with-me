package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class InitiatorRatingDto {
    private UserShortDto initiator;
    private Set<Long> eventsId;
    private Integer likes;
    private Integer dislikes;
}