package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.awt.print.Pageable;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
   Page<Event> findByInitiator(Long userId, PageRequest page);
}
