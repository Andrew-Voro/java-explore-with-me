package ru.practicum.service;

import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestsDtoLists;
import ru.practicum.dto.query.EventRequestConfirmQueryDto;

import java.util.List;

public interface RequestService {
    RequestDto saveRequest(Long userId, Long eventId, RequestDto requestDto);

    List<RequestDto> getUserRequests(Long userId);

    List<RequestDto> getUserEventRequest(Long userId, Long eventId);

    RequestDto updateRequestCancel(Long userId, Long requestId);

    RequestsDtoLists updateRequestStatusByOwner(Long userId, Long eventId, EventRequestConfirmQueryDto eventRequestConfirmQueryDto);
}
