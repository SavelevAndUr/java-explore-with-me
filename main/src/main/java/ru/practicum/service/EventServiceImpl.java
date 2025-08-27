package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsServiceIntegration statsServiceIntegration;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + newEventDto.getCategory() + " was not found"));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours from now");
        }

        Boolean paid = newEventDto.getPaid() != null ? newEventDto.getPaid() : false;
        Integer participantLimit = newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0;
        Boolean requestModeration = newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true;

        Event event = Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(newEventDto.getLocation())
                .paid(paid)
                .participantLimit(participantLimit)
                .requestModeration(requestModeration)
                .title(newEventDto.getTitle())
                .state(EventState.PENDING)
                .build();

        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findByInitiatorId(userId, pageable).stream()
                .map(event -> {
                    // Добавляем confirmedRequests и views для каждого события
                    Long confirmedRequests = requestRepository.countConfirmedRequests(event.getId());
                    Long views = statsServiceIntegration.getEventViews(event.getId());
                    event.setConfirmedRequests(confirmedRequests);
                    event.setViews(views);
                    return EventMapper.toEventShortDto(event);
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        // Добавляем confirmedRequests и views
        Long confirmedRequests = requestRepository.countConfirmedRequests(eventId);
        Long views = statsServiceIntegration.getEventViews(eventId);
        event.setConfirmedRequests(confirmedRequests);
        event.setViews(views);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEvent.getEventDate() != null &&
                updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hours from now");
        }

        updateEventFields(event, updateEvent);

        if ("SEND_TO_REVIEW".equals(updateEvent.getStateAction())) {
            event.setState(EventState.PENDING);
        } else if ("CANCEL_REVIEW".equals(updateEvent.getStateAction())) {
            event.setState(EventState.CANCELED);
        }

        Event updatedEvent = eventRepository.save(event);

        // Добавляем confirmedRequests и views
        Long confirmedRequests = requestRepository.countConfirmedRequests(eventId);
        Long views = statsServiceIntegration.getEventViews(eventId);
        updatedEvent.setConfirmedRequests(confirmedRequests);
        updatedEvent.setViews(views);

        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states,
                                             List<Long> categories, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAdminEvents(users, states, categories, rangeStart, rangeEnd, pageable)
                .stream()
                .map(event -> {
                    // Добавляем confirmedRequests и views для каждого события
                    Long confirmedRequests = requestRepository.countConfirmedRequests(event.getId());
                    Long views = statsServiceIntegration.getEventViews(event.getId());
                    event.setConfirmedRequests(confirmedRequests);
                    event.setViews(views);
                    return EventMapper.toEventFullDto(event);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updateEvent.getEventDate() != null &&
                updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Event date must be at least 1 hour from now");
        }

        if ("PUBLISH_EVENT".equals(updateEvent.getStateAction())) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if ("REJECT_EVENT".equals(updateEvent.getStateAction())) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Cannot reject published event");
            }
            event.setState(EventState.CANCELED);
        }

        updateEventFields(event, updateEvent);
        Event updatedEvent = eventRepository.save(event);

        // Добавляем confirmedRequests и views
        Long confirmedRequests = requestRepository.countConfirmedRequests(eventId);
        Long views = statsServiceIntegration.getEventViews(eventId);
        updatedEvent.setConfirmedRequests(confirmedRequests);
        updatedEvent.setViews(views);

        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer from,
                                               Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Start date must be before end date");
        }

        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(100);

        Pageable pageable;
        if ("EVENT_DATE".equals(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate"));
        } else if ("VIEWS".equals(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("views").descending());
        } else {
            pageable = PageRequest.of(from / size, size);
        }

        try {
            return eventRepository.findPublicEvents(text, categories, paid, start, end, onlyAvailable, pageable)
                    .stream()
                    .map(event -> {
                        Long confirmedRequests = requestRepository.countConfirmedRequests(event.getId());
                        Long views = statsServiceIntegration.getEventViews(event.getId());
                        event.setConfirmedRequests(confirmedRequests);
                        event.setViews(views);
                        return EventMapper.toEventShortDto(event);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ValidationException("Error processing events: " + e.getMessage());
        }
    }

    @Override
    public EventFullDto getPublicEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + id + " was not found");
        }

        // Получаем количество просмотров из статистики
        Long views = statsServiceIntegration.getEventViews(id);
        event.setViews(views);

        // Получаем количество подтвержденных запросов
        Long confirmedRequests = requestRepository.countConfirmedRequests(id);
        event.setConfirmedRequests(confirmedRequests);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return requestRepository.findByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Event does not require moderation");
        }

        List<ParticipationRequest> requests = requestRepository.findByIdIn(request.getRequestIds());

        // Проверяем, что все запросы находятся в состоянии PENDING
        for (ParticipationRequest pr : requests) {
            if (pr.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must be in PENDING state");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        long confirmedCount = requestRepository.countConfirmedRequests(eventId);
        int availableSlots = event.getParticipantLimit() - (int) confirmedCount;

        if ("CONFIRMED".equals(request.getStatus())) {
            if (availableSlots <= 0) {
                throw new ConflictException("Participant limit reached");
            }

            for (ParticipationRequest pr : requests) {
                if (availableSlots > 0) {
                    pr.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toParticipationRequestDto(pr));
                    availableSlots--;
                } else {
                    pr.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toParticipationRequestDto(pr));
                }
            }
        } else if ("REJECTED".equals(request.getStatus())) {
            for (ParticipationRequest pr : requests) {
                pr.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(pr));
            }
        }

        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private void updateEventFields(Event event, Object updateRequest) {
        if (updateRequest instanceof UpdateEventUserRequest userRequest) {
            updateEventFromUserRequest(event, userRequest);
        } else if (updateRequest instanceof UpdateEventAdminRequest adminRequest) {
            updateEventFromAdminRequest(event, adminRequest);
        }
    }

    private void updateEventFromUserRequest(Event event, UpdateEventUserRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(request.getLocation()));
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }

    private void updateEventFromAdminRequest(Event event, UpdateEventAdminRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(request.getLocation()));
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }
}