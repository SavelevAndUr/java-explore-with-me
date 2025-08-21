package ru.practicum.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EndpointHit;
import ru.practicum.repository.StatRecord;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatMapper {
    public static StatRecord toStatRecord(EndpointHit endpointHit) {
        StatRecord statRecord = new StatRecord();
        statRecord.setApp(endpointHit.getApp());
        statRecord.setUri(endpointHit.getUri());
        statRecord.setIp(endpointHit.getIp());
        statRecord.setTimestamp(endpointHit.getTimestamp());
        return statRecord;
    }
}
