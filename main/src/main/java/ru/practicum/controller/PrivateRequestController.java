package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Integer userId, @RequestParam(required = false) Integer eventId) {
        log.info("Creating request for event={} from user={}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getRequestsByUserId(@PathVariable Integer userId) {
        log.info("Searching request for user={}", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping(value = "/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Integer userId, @PathVariable Integer requestId) {
        log.info("Canceling request={} for user={}", requestId, userId);
        return requestService.cancelRequest(userId, requestId);
    }
}