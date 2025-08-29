package ru.practicum.mapper;

import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;

public class LocationMapper {
    public static Location toNewEntity(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}