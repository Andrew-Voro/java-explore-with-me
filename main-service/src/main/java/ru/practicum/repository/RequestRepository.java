package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.State;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequester(User user);

    List<Request> findByEvent(Event event);

    Request findByIdAndRequester(Long requestId, User user);

    List<Request> findByEventAndIdIn(Event event, List<Long> ids);

    List<Request> findByRequesterIn(List<User> requesters);

    Optional<Request> findByEventAndRequester(Event event, User requester);

    List<Request> findByEventAndStatus(Event event, State state);
}
