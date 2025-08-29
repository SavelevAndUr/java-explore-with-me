package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.LocationDto;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Location;
import ru.practicum.repository.LocationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public Location saveLocation(LocationDto locationDto) {
        return locationRepository.save(LocationMapper.toNewEntity(locationDto));
    }
}