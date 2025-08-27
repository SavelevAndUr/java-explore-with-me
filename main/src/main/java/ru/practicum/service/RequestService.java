package ru.practicum.service;

import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestStatusUpdateDto;
import ru.practicum.dto.RequestsByStatusDto;
import ru.practicum.model.Event;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getRequestsByUserId(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getRequestsByEventId(Long eventId);

    RequestsByStatusDto updateRequestsStatusByEvent(RequestStatusUpdateDto statusUpdateDto, Event event);
}