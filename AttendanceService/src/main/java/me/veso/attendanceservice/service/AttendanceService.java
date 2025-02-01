package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.dto.AttendanceCreationDto;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.entity.Attendance;
import me.veso.attendanceservice.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserIdService userIdService;
    private final CategoryIdService categoryService;

    public AttendanceDetailsDto createAttendance(AttendanceCreationDto attendanceCreationDto) {
        Attendance attendance = new Attendance()
                .setStatus(attendanceCreationDto.getStatus())
                .setUserId(userIdService.saveIdLongIfNotExists(attendanceCreationDto.getUserId()))
                .setCategoryId(categoryService.saveIdLongIfNotExists(attendanceCreationDto.getCategoryId()));

        return new AttendanceDetailsDto(attendanceRepository.save(attendance));
    }

    public List<AttendanceDetailsDto> getAttendanceForCategory(String categoryId) {
        return attendanceRepository.findAllByCategory_CategoryId(categoryId).stream().map(AttendanceDetailsDto::new).toList();
    }

    public List<AttendanceDetailsDto> getAttendanceForUser(Long userId) {
        return attendanceRepository.findAllByUser_UserId(userId).stream().map(AttendanceDetailsDto::new).toList();
    }
}
