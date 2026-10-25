package com.mediqueue.mediqueue_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(length = 1000)
    private String diagnosis;

    @Column(length = 2000)
    private String medicines;

    @Column(length = 1000)
    private String advice;

    private LocalDateTime createdAt = LocalDateTime.now();
}