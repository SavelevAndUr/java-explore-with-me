package ru.practicum.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 20, max = 2000)
    @Column(name = "annotation", nullable = false)
    private String annotation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Size(min = 20, max = 7000)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Embedded
    private Location location;

    @Column(name = "paid", nullable = false)
    @Builder.Default
    private Boolean paid = false;

    @Column(name = "participant_limit")
    @Builder.Default
    private Integer participantLimit = 0;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    @Builder.Default
    private Boolean requestModeration = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    @Builder.Default
    private EventState state = EventState.PENDING;

    @NotBlank
    @Size(min = 3, max = 120)
    @Column(name = "title", nullable = false)
    private String title;

    @Transient
    @Builder.Default
    private Long views = 0L;

    @Transient
    @Builder.Default
    private Long confirmedRequests = 0L;

    public boolean isParticipantLimitReached(Long currentConfirmedRequests) {
        return participantLimit > 0 && currentConfirmedRequests >= participantLimit;
    }

    public boolean requiresModeration() {
        return requestModeration;
    }

    public boolean isAvailableForParticipation(Long currentConfirmedRequests) {
        return participantLimit == 0 || currentConfirmedRequests < participantLimit;
    }

    public boolean canBePublished() {
        return state == EventState.PENDING;
    }

    public boolean canBeRejected() {
        return state != EventState.PUBLISHED;
    }

    public boolean canBeUpdatedByUser() {
        return state == EventState.PENDING || state == EventState.CANCELED;
    }

    public boolean isPublished() {
        return state == EventState.PUBLISHED;
    }

    public boolean isCanceled() {
        return state == EventState.CANCELED;
    }

    public boolean isPending() {
        return state == EventState.PENDING;
    }

    public void publish() {
        this.state = EventState.PUBLISHED;
        this.publishedOn = LocalDateTime.now();
    }

    public void cancel() {
        this.state = EventState.CANCELED;
    }

    public void sendToReview() {
        this.state = EventState.PENDING;
    }

    public boolean isEventDateValid() {
        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }

    public boolean isEventDateValidForAdmin() {
        return eventDate.isAfter(LocalDateTime.now().plusHours(1));
    }
}