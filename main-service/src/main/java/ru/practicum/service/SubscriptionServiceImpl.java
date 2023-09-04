package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.InitiatorCountUsersDto;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.enums.State;
import ru.practicum.handler.exception.ObjectNotFoundException;
import ru.practicum.mapper.SubscriptionMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    @Override
    public SubscriptionDto saveSubscription(SubscriptionDto subscriptionDto) {

        User user = userRepository.findById(subscriptionDto.getUser()).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        User initiator = userRepository.findById(subscriptionDto.getInitiator()).orElseThrow(() -> new ObjectNotFoundException("Initiator not found"));
        return SubscriptionMapper.toSubscriptionDto(subscriptionRepository.save(SubscriptionMapper.toDtoSubscription(subscriptionDto, user, initiator)));
    }

    @Transactional
    @Override
    public SubscriptionDto getSubscriptionByUserIdAndInitiatorId(Long userId, Long initiatorId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        userRepository.findById(initiatorId).orElseThrow(() -> new ObjectNotFoundException("Initiator not found"));
        Subscription subscription = subscriptionRepository.getByUserIdAndInitiatorId(userId, initiatorId).orElseThrow(() -> new ObjectNotFoundException("Subscription not found"));
        return SubscriptionMapper.toSubscriptionDto(subscription);
    }

    @Transactional
    @Override
    public void deleteSubscriptionByUserIdAndInitiatorId(Long userId, Long initiatorId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        userRepository.findById(initiatorId).orElseThrow(() -> new ObjectNotFoundException("Initiator not found"));
        subscriptionRepository.getByUserIdAndInitiatorId(userId, initiatorId).orElseThrow(() -> new ObjectNotFoundException("Subscription not found"));
        subscriptionRepository.deleteByUserIdAndInitiatorId(userId, initiatorId);
    }

    @Transactional
    @Override
    public List<User> getUsersOfInitiator(Long initiatorId, Long from, Long size) {
        userRepository.findById(initiatorId).orElseThrow(() -> new ObjectNotFoundException("Initiator not found"));
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return subscriptionRepository.getUsersOfInitiator(initiatorId, page);
    }

    @Transactional
    @Override
    public List<User> getInitiatorsOfUser(Long userId, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return subscriptionRepository.getInitiatorsOfUser(userId, page);
    }

    @Transactional
    @Override
    public List<Event> getEventsOfSubscriptionsByUserId(Long userId, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return  subscriptionRepository.getEventsOfSubscriptionsByUserId(userId, State.PUBLISHED,page);
    }
    @Transactional
    @Override
    public List<InitiatorCountUsersDto> getInitiatorOrderedByNumberOfUsers(Long from, Long size) {
        List<InitiatorCountUsersDto> results= new ArrayList<>();
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        List<Object[]> resultList = subscriptionRepository.getInitiatorOfThemNumberOfUsers(page);
        for (Object[] resultType : resultList) {
            InitiatorCountUsersDto initiatorCountUsersDto = InitiatorCountUsersDto.builder().Initiator((String) resultType[0]).countOfSubscribers((Long) resultType[1]).build();
            results.add(initiatorCountUsersDto);
        }
        List<InitiatorCountUsersDto> sorted = results.stream().sorted((x,y)->(y.getCountOfSubscribers().compareTo(x.getCountOfSubscribers()))).collect(Collectors.toList());
        return sorted;
    }
}