package ru.practicum.service;

import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestStatusUpdateDto;
import ru.practicum.dto.RequestsByStatusDto;
import ru.practicum.model.Event;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Integer userId, Integer eventId);

    List<RequestDto> getRequestsByUserId(Integer userId);

    RequestDto cancelRequest(Integer userId, Integer requestId);

    List<RequestDto> getRequestsByEventId(Integer eventId);

    RequestsByStatusDto updateRequestsStatusByEvent(RequestStatusUpdateDto statusUpdateDto, Event event);
}