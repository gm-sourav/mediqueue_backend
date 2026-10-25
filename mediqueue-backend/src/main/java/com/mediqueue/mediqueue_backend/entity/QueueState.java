package com.mediqueue.mediqueue_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "queue_state",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "queue_date"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class QueueState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDate queueDate;

    @Column(nullable = false)
    private Integer currentToken = 0;
}