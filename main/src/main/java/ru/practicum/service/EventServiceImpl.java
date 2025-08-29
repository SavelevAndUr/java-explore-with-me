package ru.practicum.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.*;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {
    private static final Short HOURS_BEFORE_EVENT = 2;
    private static final Short HOUR_BEFORE_EVENT = 1;
    private static final String START = "2000-01-01 00:00:00";
    private static final String END = "2100-01-01 00:00:00";
    private static final String URI = "/events/";
    private static final String UNIQUE = "true";
    private static final String APPLICATION_NAME = "ewm-main-service";
    private static final String INCORRECT_TIME_MSG = "Incorrect time input";
    private static final String INCORRECT_TIME_REASON = "The time of event must be at least in 2 hours before published";
    private static final String INCORRECT_STATE_MSG = "Incorrect state for updating";
    private static final String INCORRECT_STATE_REASON = "The event can't be published when updating";
    private static final String INCORRECT_EVENT_ANNOTATION = "Event annotation is incorrect";
    private static final String INCORRECT_EVENT_DESCRIPTION = "Event description is incorrect";
    private static final String INCORRECT_DATA_INPUT_MSG = "Incorrect data input";
    private static final String NOT_FOUND_EVENT_MSG = "Event not found";
    private static final String NOT_FOUND_USER_MSG = "User not found";
    private static final String NOT_FOUND_ID_REASON = "Incorrect Id";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final StatsClient statsClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Event> findById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Category category = categoryService.getCategoryEntity(newEventDto.getCategory());
        User initiator = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_MSG, NOT_FOUND_ID_REASON));
        Location location = locationService.saveLocation(newEventDto.getLocation());

        Event event = EventMapper.toNewEntity(newEventDto, category, initiator, location);
        checkTime(event);

        if (event.getAnnotation() == null || event.getAnnotation().isBlank()) {
            throw new BadRequestException(INCORRECT_EVENT_ANNOTATION, NOT_FOUND_ID_REASON);
        }
        if (event.getDescription() == null || event.getDescription().isBlank()) {
            throw new BadRequestException(INCORRECT_EVENT_DESCRIPTION, NOT_FOUND_ID_REASON);
        }

        event = eventRepository.save(event);
        Integer views = 0;
        log.info("Created event {}", event);
        return EventMapper.toFullDto(event, views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUserId(Long userId, PageRequest page) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        Map<Long, Integer> views = getStats(events);
        log.info("Getting events {}", events);
        return EventMapper.toShortDtos(events, views);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getStats(List<Event> events) {
        Map<Long, Integer> views = new HashMap<>();
        for (Event event : events) {
            Long id = event.getId();
            Integer view = getStats(id);
            views.put(id, view);
        }
        return views;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventsById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        Integer views = getStats(event.getId());
        log.info("Getting event {}", event);
        return EventMapper.toFullDto(event, views);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEventUserDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));

        checkState(userId, event);

        Category category = Objects.nonNull(updateEventUserDto.getCategory())
                ? categoryService.getCategoryEntity(updateEventUserDto.getCategory()) : null;
        Location location = Objects.nonNull(updateEventUserDto.getLocation())
                ? locationService.saveLocation(updateEventUserDto.getLocation()) : null;

        event = EventMapper.toUpdatedEntity(event, updateEventUserDto, category, location);
        checkTime(event);

        Integer views = getStats(event.getId());
        log.info("Found views {}", views);

        event = eventRepository.save(event);
        log.info("Updated event {}", event);

        views = getStats(event.getId());
        log.info("Found views {}", views);

        return EventMapper.toFullDto(event, views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByEventId(Long userId, Long eventId) {
        return requestService.getRequestsByEventId(eventId);
    }

    @Override
    @Transactional
    public RequestsByStatusDto updateEventRequestsStatus(Long eventId, Long userId,
                                                         RequestStatusUpdateDto statusUpdateDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        return requestService.updateRequestsStatusByEvent(statusUpdateDto, event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdminFilters(List<Long> users, List<String> statesStr, List<Integer> categories,
                                                      String rangeStart, String rangeEnd, PageRequest page) {
        LocalDateTime start = parseDateTime(rangeStart);
        LocalDateTime end = parseDateTime(rangeEnd);

        List<Event> events = getEventsByFilters(null, null, users, statesStr, categories, start, end, page);
        Map<Long, Integer> views = getStats(events);
        log.info("Getting events {}", events);
        return EventMapper.toFullDtos(events, views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByPublicFilters(String text, List<Integer> categories, Boolean paid,
                                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                        String sort, PageRequest page, HttpServletRequest request) {
        LocalDateTime start = parseDateTime(rangeStart);
        LocalDateTime end = parseDateTime(rangeEnd);

        if (start != null && end != null && end.isBefore(start)) {
            log.info("Date is incorrect");
            throw new BadRequestException(NOT_FOUND_EVENT_MSG, INCORRECT_DATA_INPUT_MSG);
        }

        List<Event> events = getEventsByFilters(text, paid, null,
                List.of(State.PUBLISHED.toString()), categories, start, end, page);

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 ||
                            event.getParticipantLimit() > getConfirmedRequestsCount(event))
                    .collect(Collectors.toList());
        }

        Map<Long, Integer> views = getStats(events);
        saveStats(request);
        log.info("Getting events {}", events);

        List<EventShortDto> shortDtos = EventMapper.toShortDtos(events, views);
        return sortDto(sort, shortDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_EVENT_MSG, NOT_FOUND_ID_REASON));
        Integer views = getStats(event.getId());
        log.info("Found views {}", views);
        saveStats(request);
        log.info("Getting event {}", event);
        return EventMapper.toFullDto(event, views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByIds(List<Long> eventsId) {
        return eventRepository.findAllById(eventsId);
    }

    private Integer getStats(Long id) {
        try {
            ResponseEntity<List<ViewStats>> response = statsClient.getStats(START, END, URI + id, UNIQUE);
            List<ViewStats> responseStatsDtos = response.getBody();
            if (responseStatsDtos != null && !responseStatsDtos.isEmpty()) {
                return responseStatsDtos.get(0).getHits();
            }
            return 0;
        } catch (Exception e) {
            log.warn("Failed to get stats for event {}: {}", id, e.getMessage());
            return 0;
        }
    }

    private void saveStats(HttpServletRequest request) {
        try {
            EndpointHit requestStatsDto = EndpointHit.builder()
                    .app(APPLICATION_NAME)
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now().format(FORMAT))
                    .build();
            statsClient.createStats(requestStatsDto);
        } catch (Exception e) {
            log.warn("Failed to save stats: {}", e.getMessage());
        }
    }

    private void checkState(Long userId, Event event) {
        if (Objects.nonNull(userId)) {
            if (Objects.equals(event.getState(), State.PUBLISHED)) {
                throw new ConflictException(INCORRECT_STATE_MSG, INCORRECT_STATE_REASON);
            }
        }
    }

    private void checkTime(Event event) {
        if (event.getEventDate().minusHours(HOURS_BEFORE_EVENT).isBefore(event.getCreatedOn())) {
            throw new BadRequestException(INCORRECT_TIME_MSG, INCORRECT_TIME_REASON);
        }
        if (Objects.nonNull(event.getPublishedOn())) {
            if (event.getEventDate().minusHours(HOUR_BEFORE_EVENT).isBefore(event.getPublishedOn())) {
                throw new BadRequestException(INCORRECT_TIME_MSG, INCORRECT_TIME_REASON);
            }
        }
    }

    private LocalDateTime parseDateTime(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTime, FORMAT);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format", "Use yyyy-MM-dd HH:mm:ss");
        }
    }

    private List<EventShortDto> sortDto(String sort, List<EventShortDto> shortDtos) {
        if (Objects.isNull(sort)) {
            return shortDtos;
        }

        switch (sort.toUpperCase()) {
            case "EVENT_DATE":
                return shortDtos.stream()
                        .sorted(Comparator.comparing(EventShortDto::getEventDate))
                        .collect(Collectors.toList());
            case "VIEWS":
                return shortDtos.stream()
                        .sorted(Comparator.comparingInt(EventShortDto::getViews).reversed())
                        .collect(Collectors.toList());
            default:
                return shortDtos;
        }
    }

    private int getConfirmedRequestsCount(Event event) {
        return (int) event.getRequests().stream()
                .filter(request -> RequestStatus.CONFIRMED.equals(request.getStatus()))
                .count();
    }

    private List<Event> getEventsByFilters(String text, Boolean paid, List<Long> users, List<String> statesStr,
                                           List<Integer> categories, LocalDateTime start, LocalDateTime end,
                                           PageRequest page) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            predicates.add(event.get("initiator").get("id").in(users));
        }

        if (statesStr != null && !statesStr.isEmpty()) {
            List<State> states = statesStr.stream()
                    .map(state -> {
                        try {
                            return State.valueOf(state.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!states.isEmpty()) {
                predicates.add(event.get("state").in(states));
            }
        }

        if (categories != null && !categories.isEmpty()) {
            predicates.add(event.get("category").get("id").in(categories));
        }

        if (end != null) {
            predicates.add(builder.lessThanOrEqualTo(event.get("eventDate"), end));
        }

        if (start != null) {
            predicates.add(builder.greaterThanOrEqualTo(event.get("eventDate"), start));
        }

        if (text != null && !text.isBlank()) {
            String searchText = "%" + text.toLowerCase() + "%";
            Predicate annotation = builder.like(builder.lower(event.get("annotation")), searchText);
            Predicate description = builder.like(builder.lower(event.get("description")), searchText);
            Predicate title = builder.like(builder.lower(event.get("title")), searchText);
            predicates.add(builder.or(annotation, description, title));
        }

        if (paid != null) {
            predicates.add(builder.equal(event.get("paid"), paid));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(builder.asc(event.get("eventDate")));

        return entityManager.createQuery(query)
                .setFirstResult((int) page.getOffset())
                .setMaxResults(page.getPageSize())
                .getResultList();
    }
}