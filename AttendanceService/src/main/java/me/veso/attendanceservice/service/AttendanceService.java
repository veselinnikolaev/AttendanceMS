package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.attendanceservice.client.RabbitClient;
import me.veso.attendanceservice.dto.AttendanceCreationDto;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.entity.Attendance;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserIdService userIdService;
    private final CategoryIdService categoryService;
    private final RabbitClient rabbitClient;

    public AttendanceDetailsDto createAttendance(AttendanceCreationDto attendanceCreationDto) {
        log.info("Creating attendance for user ID {} in category ID {}", attendanceCreationDto.userId(), attendanceCreationDto.categoryId());

        UserId userId = userIdService.saveIdLongIfNotExists(attendanceCreationDto.userId());
        CategoryId categoryId = categoryService.saveIdLongIfNotExists(attendanceCreationDto.categoryId());

        Attendance attendance = new Attendance()
                .setStatus(attendanceCreationDto.status())
                .setUserId(userId)
                .setCategoryId(categoryId);

        Attendance attendanceSaved = attendanceRepository.save(attendance);

        AttendanceDetailsDto attendanceDetailsDto = new AttendanceDetailsDto(attendanceSaved);
        rabbitClient.notifyAssigmentCreated(attendanceDetailsDto);

        log.info("Attendance created successfully for user ID {} in category ID {}", attendanceCreationDto.userId(), attendanceCreationDto.categoryId());
        return attendanceDetailsDto;
    }

    public List<AttendanceDetailsDto> getAttendanceForCategory(String categoryId) {
        log.info("Fetching attendance records for category ID {}", categoryId);

        List<AttendanceDetailsDto> attendanceDetails = attendanceRepository.findAllByCategory_CategoryId(categoryId)
                .stream()
                .map(AttendanceDetailsDto::new)
                .toList();

        log.info("Found {} attendance records for category ID {}", attendanceDetails.size(), categoryId);
        return attendanceDetails;
    }

    public List<AttendanceDetailsDto> getAttendanceForUser(Long userId) {
        log.info("Fetching attendance records for user ID {}", userId);

        List<AttendanceDetailsDto> attendanceDetails = attendanceRepository.findAllByUser_UserId(userId)
                .stream()
                .map(AttendanceDetailsDto::new)
                .toList();

        log.info("Found {} attendance records for user ID {}", attendanceDetails.size(), userId);
        return attendanceDetails;
    }

    @Transactional
    public void deleteByCategoryId(String id) {
        log.info("Deleting attendance records for category ID {}", id);

        attendanceRepository.deleteAllByCategoryId(id);

        log.info("Deleted category with ID {}", id);
    }
}
