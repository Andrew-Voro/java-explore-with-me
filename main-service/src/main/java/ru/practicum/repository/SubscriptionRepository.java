package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.enums.State;
import ru.practicum.model.Event;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> getByUserIdAndInitiatorId(Long userId, Long initiatorId);

    void deleteByUserIdAndInitiatorId(Long userId, Long initiatorId);

    @Query("select u from Subscription as sub join User as u on sub.user.id = u.id  where sub.initiator.id=:initiatorId")
    List<User> getUsersOfInitiator(@Param("initiatorId") Long initiatorId, PageRequest page);

    @Query("select u from Subscription as sub join User as u on sub.initiator.id = u.id where sub.user.id=:userId")
    List<User> getInitiatorsOfUser(@Param("userId") Long userId, PageRequest page);

    @Query("select ev from Subscription as sub join Event as ev on sub.initiator.id = ev.initiator where sub.user.id=:userId and ev.stateAction=:state")
    List<Event> getEventsOfSubscriptionsByUserId(@Param("userId") Long userId, @Param("state") State state, PageRequest page);


    @Query(value = "select sub.initiator.name, count(sub.initiator.name) as num from Subscription as sub join User as u on sub.initiator.id = u.id group by sub.initiator.name")
    List<Object[]> getInitiatorOfThemNumberOfUsers(PageRequest page);
}
