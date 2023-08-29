package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime created;
    @ManyToOne
    // @JoinColumn(name = "event")
            Event event;
    // Long event;
    @ManyToOne
    @JoinColumn(name = "requester")
    User requester;
    @Enumerated(EnumType.STRING)
    State status;
}
