package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {
    // List<Hit> findByAppAndUri(String app, String uri);

    List<Hit> findByUriAndTimestampsIsAfterAndTimestampsIsBefore(String uri, LocalDateTime startTime, LocalDateTime endTime);

    List<Hit> findByTimestampsIsAfterAndTimestampsIsBefore(LocalDateTime startTime, LocalDateTime endTime);
}
