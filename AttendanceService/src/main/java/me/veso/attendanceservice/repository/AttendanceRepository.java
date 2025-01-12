package me.veso.attendanceservice.repository;

import me.veso.attendanceservice.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findAllByCategory_CategoryId(Long categoryId);

    List<Attendance> findAllByUser_UserId(Long userId);
}
