package me.veso.attendanceservice.repository;

import me.veso.attendanceservice.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendancePagingRepository extends PagingAndSortingRepository<Attendance, Long> {
    Page<Attendance> findAllByCategory_CategoryId(String categoryId, Pageable pageable);

    Page<Attendance> findAllByUser_UserId(Long userId, Pageable pageable);
}
