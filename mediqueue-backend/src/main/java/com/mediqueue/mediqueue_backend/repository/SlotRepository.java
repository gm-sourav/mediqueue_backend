package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.Slot;
import com.mediqueue.mediqueue_backend.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    boolean existsByDoctorIdAndSlotDateAndStartTime(Long doctorId, LocalDate date, LocalTime startTime);

    List<Slot> findByDoctorIdAndSlotDateOrderByStartTime(Long doctorId, LocalDate date);

    List<Slot> findByDoctorIdAndSlotDateAndStatusOrderByStartTime(Long doctorId, LocalDate date, SlotStatus status);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Slot s SET s.status = :newStatus WHERE s.id = :id AND s.status = :expected")
    int updateStatusIfCurrent(@Param("id") Long id,
                              @Param("expected") SlotStatus expected,
                              @Param("newStatus") SlotStatus newStatus);
}