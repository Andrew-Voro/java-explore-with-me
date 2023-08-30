package ru.practicum.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.query.EventDynamicQueryDto;
import ru.practicum.enums.Sort;
import ru.practicum.enums.State;
import ru.practicum.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class CustomEventRepository {
    private final EntityManager entityManager;

    public List<Event> findEventByAdmin(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (eventDynamicQueryDto.getUsers().isPresent()) {
            Predicate initiators = root.get("initiator").in(eventDynamicQueryDto.getUsers().get());
            predicates.add(initiators);
        }
        if (eventDynamicQueryDto.getStates().isPresent()) {
            Predicate states = root.get("stateAction").in(eventDynamicQueryDto.getStates().get());
            predicates.add(states);
        }
        if (eventDynamicQueryDto.getCategories().isPresent()) {
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
        query.setFirstResult(from.intValue());
        query.setMaxResults(size.intValue());
        List<Event> result = query.getResultList();

        return result;
    }

    public List<Event> findEvent(EventDynamicQueryDto eventDynamicQueryDto, Long from, Long size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (eventDynamicQueryDto.getText().isPresent()) {
            String searchText = "%" + eventDynamicQueryDto.getText().get().toLowerCase() + "%";
            Predicate searchAnnotation = builder.like(root.get("annotation"), searchText);
            Predicate searchDescription = builder.like(root.get("description"), searchText);
            predicates.add(builder.or(searchAnnotation, searchDescription));
        }
        if (eventDynamicQueryDto.getCategories().isPresent()) {
            Predicate categories = root.get("category").in(eventDynamicQueryDto.getCategories().get());
            predicates.add(categories);
        }
        if (eventDynamicQueryDto.getPaid().isPresent()) {
            Predicate paid = builder.equal(root.get("paid"), eventDynamicQueryDto.getPaid().get());
            predicates.add(paid);
        }
        if (eventDynamicQueryDto.getRangeStart() != null) {
            Predicate start = builder.greaterThan(root.get("eventDate"), eventDynamicQueryDto.getRangeStart());
            predicates.add(start);
        }
        if (eventDynamicQueryDto.getRangeEnd() != null) {
            Predicate end = builder.lessThan(root.get("eventDate"), eventDynamicQueryDto.getRangeEnd());
            predicates.add(end);
        }
        if (eventDynamicQueryDto.getOnlyAvailable().isPresent() && eventDynamicQueryDto.getOnlyAvailable().get() == true) {
            Expression<Integer> confirmed = root.get("confirmed_requests");
            Expression<Integer> limit = root.get("participant_limit");
            predicates.add(builder.notEqual(builder.diff(confirmed, limit), 0));

        }
        predicates.add(builder.equal(root.get("stateAction"), State.PUBLISHED));

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        List<Event> result = entityManager.createQuery(criteriaQuery)
                .setFirstResult(from.intValue())
                .setMaxResults(size.intValue())
                .getResultList();
        return result;
    }
        /*if (eventDynamicQueryDto.getSort().isPresent()) {
            if (eventDynamicQueryDto.getSort().get().equals(Sort.EVENT_DATE)) {
                criteriaQuery.select(root)
                        .where(builder.or(predicates.toArray(new Predicate[predicates.size()]))).orderBy(builder.desc(root.get("eventDate")));
                TypedQuery<Event> query = entityManager.createQuery(criteriaQuery);
                query.setFirstResult(0);
                query.setMaxResults(10);
                List<Event> result = query.getResultList();
                return result;
            }
        } else {
            criteriaQuery.select(root)
                    .where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        }
        TypedQuery<Event> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(0);
        query.setMaxResults(10);
        List<Event> result = query.getResultList();
        return result;
    }*/
}
