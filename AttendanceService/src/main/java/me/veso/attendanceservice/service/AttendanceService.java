package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.config.MessageQueueConfig;
import me.veso.attendanceservice.dto.AttendanceCreationDto;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.entity.Attendance;
import me.veso.attendanceservice.repository.AttendanceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserIdService userIdService;
    private final CategoryIdService categoryService;
    private final RabbitTemplate rabbitTemplate;

    public AttendanceDetailsDto createAttendance(AttendanceCreationDto attendanceCreationDto) {
        Attendance attendance = new Attendance()
                .setStatus(attendanceCreationDto.status())
                .setUserId(userIdService.saveIdLongIfNotExists(attendanceCreationDto.userId()))
                .setCategoryId(categoryService.saveIdLongIfNotExists(attendanceCreationDto.categoryId()));

        Attendance attendanceSaved = attendanceRepository.save(attendance);

        rabbitTemplate.convertAndSend(MessageQueueConfig.EXCHANGE_NAME, "attendance.created", new AttendanceDetailsDto(attendanceSaved));

        return new AttendanceDetailsDto(attendanceSaved);
    }

    public List<AttendanceDetailsDto> getAttendanceForCategory(String categoryId) {
        return attendanceRepository.findAllByCategory_CategoryId(categoryId).stream().map(AttendanceDetailsDto::new).toList();
    }

    public List<AttendanceDetailsDto> getAttendanceForUser(Long userId) {
        return attendanceRepository.findAllByUser_UserId(userId).stream().map(AttendanceDetailsDto::new).toList();
    }

    @Transactional
    public void deleteByCategoryId(String id) {
        attendanceRepository.deleteAllByCategoryId(id);
    }
}
