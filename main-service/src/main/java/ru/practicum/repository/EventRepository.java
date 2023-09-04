package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.State;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiator(Long userId, PageRequest page);

    Optional<Event> findByIdAndInitiator(Long eventId, Long userId);

    Optional<Event> findByIdAndStateAction(Long id, State state);

    List<Event> findBycategory(Category category);
}
