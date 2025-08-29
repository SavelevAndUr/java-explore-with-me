package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LikeDislike {
    private Map<Event, Integer> likes;
    private Map<Event, Integer> dislike;
}
