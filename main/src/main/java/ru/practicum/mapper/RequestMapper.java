package ru.practicum.mapper;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        if (request == null) {
            return null;
        }

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent() != null ? request.getEvent().getId() : null)
                .requester(request.getRequester() != null ? request.getRequester().getId() : null)
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .build();
    }

    public static ParticipationRequest toParticipationRequest(ParticipationRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return ParticipationRequest.builder()
                .id(requestDto.getId())
                .status(requestDto.getStatus() != null ?
                        RequestStatus.valueOf(requestDto.getStatus()) : RequestStatus.PENDING)
                .build();
    }
}