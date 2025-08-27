package ru.practicum.model;

import jakarta.persistence.Column;
import lombok.*;

import jakarta.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;
}