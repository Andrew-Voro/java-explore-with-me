package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events", schema = "public", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})})
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String annotation;
    @ManyToOne
    @JoinColumn(name = "category")
    Category category;
    @Column(name = "confirmed_requests")
    Long confirmedRequests;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    Long initiator;
    Float lat;
    Float lon;
    Boolean paid;
    @Column(name = "participant_limit")
    Long participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Column(name = "state_action")
    @Enumerated(EnumType.STRING)
    State stateAction;
    String title;
    @JsonBackReference
    //@ManyToMany(mappedBy = "events")
    // @JsonManagedReference
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id")
    )

    List<Compilation> compilations;
    Long views;
}
