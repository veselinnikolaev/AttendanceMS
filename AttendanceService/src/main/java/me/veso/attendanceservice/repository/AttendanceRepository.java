package me.veso.attendanceservice.repository;

import me.veso.attendanceservice.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findAllByCategory_CategoryId(String categoryId);

    List<Attendance> findAllByUser_UserId(Long userId);

    @Modifying
    @Query("DELETE FROM Attendance a WHERE a.category.categoryId = :categoryId")
    void deleteAllByCategoryId(@Param("categoryId") String categoryId);
}
