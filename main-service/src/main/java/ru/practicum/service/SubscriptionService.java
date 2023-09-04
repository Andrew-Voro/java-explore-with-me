package ru.practicum.service;

import ru.practicum.dto.InitiatorCountUsersDto;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto saveSubscription(SubscriptionDto subscriptionDto);

    SubscriptionDto getSubscriptionByUserIdAndInitiatorId(Long userId, Long initiatorId);

    void deleteSubscriptionByUserIdAndInitiatorId(Long userId, Long initiatorId);

    List<User> getUsersOfInitiator(Long initiatorId, Long from, Long size);

    List<User> getInitiatorsOfUser(Long userId, Long from, Long size);

    List<Event> getEventsOfSubscriptionsByUserId(Long userId, Long from, Long size);

    List<InitiatorCountUsersDto> getInitiatorOrderedByNumberOfUsers(Long from, Long size);
}
