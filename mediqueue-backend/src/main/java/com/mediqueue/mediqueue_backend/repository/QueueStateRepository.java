package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.QueueState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface QueueStateRepository extends JpaRepository<QueueState, Long> {
    Optional<QueueState> findByDoctorIdAndQueueDate(Long doctorId, LocalDate date);
}