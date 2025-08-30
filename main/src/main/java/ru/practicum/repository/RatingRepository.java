package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.Rate;
import ru.practicum.model.Rating;
import ru.practicum.model.RatingPK;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingPK> {
    List<Rating> findAllByEventAndRate(Event event, Rate like);

    List<Rating> findAllByEventIn(List<Event> events);

}
