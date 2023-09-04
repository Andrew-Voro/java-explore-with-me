package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionMapper {
    public static SubscriptionDto toSubscriptionDto(Subscription subscription) {
        return new SubscriptionDto(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getInitiator().getId()
        );
    }

    public static Subscription toDtoSubscription(SubscriptionDto subscriptionDto, User user, User initiator) {
        return new Subscription(
                subscriptionDto.getId(),
                user,
                initiator
        );
    }
}