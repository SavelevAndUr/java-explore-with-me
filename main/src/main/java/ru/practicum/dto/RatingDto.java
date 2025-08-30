package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingDto {
    private Long eventId;
    private Integer likes;
    private Integer dislikes;
}
