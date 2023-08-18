package ru.practicum.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class CustomEventRepository {
    private final EntityManager entityManager;

    public Page<Event> findEvent(EventDynamicQueryDto eventDynamicQueryDto, Pageable page) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (eventDynamicQueryDto.getUsers() != null) {
            Predicate initiators = root.get("initiator").in(eventDynamicQueryDto.getUsers().get());
            predicates.add(initiators);
        }
        if (eventDynamicQueryDto.getStates() != null) {
            Predicate states = root.get("stateAction").in(eventDynamicQueryDto.getStates().get());
            predicates.add(states);
        }
        if (eventDynamicQueryDto.getCategories() != null) {
            Predicate categories = root.get("category").in(eventDynamicQueryDto.getCategories().get());
            predicates.add(categories);
        }
        if (eventDynamicQueryDto.getRangeStart() != null) {
            Predicate start = builder.greaterThan(root.get("eventDate"), eventDynamicQueryDto.getRangeStart());
            predicates.add(start);
        }
        if (eventDynamicQueryDto.getRangeEnd() != null) {
            Predicate end = builder.lessThan(root.get("eventDate"), eventDynamicQueryDto.getRangeEnd());
            predicates.add(end);
        }
        criteriaQuery.select(root)
                .where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<Event> query = entityManager.createQuery(criteriaQuery);
        long totalRows = query.getResultList().size();
        Page<Event> result = new PageImpl<>(query.getResultList(), page, totalRows);

        return result;
    }
}
