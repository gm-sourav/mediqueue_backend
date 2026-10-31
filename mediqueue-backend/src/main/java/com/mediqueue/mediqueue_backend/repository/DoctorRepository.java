package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserEmail(String email);

    @Query("SELECT d FROM Doctor d WHERE " +
            "(:departmentId IS NULL OR d.department.id = :departmentId) AND " +
            "(:name IS NULL OR LOWER(d.user.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    List<Doctor> search(@Param("departmentId") Long departmentId,
                        @Param("name") String name);
}