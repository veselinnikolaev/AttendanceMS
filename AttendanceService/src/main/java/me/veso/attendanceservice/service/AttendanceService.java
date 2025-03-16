package me.veso.attendanceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.veso.attendanceservice.client.RabbitClient;
import me.veso.attendanceservice.dto.AttendanceCreationDto;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.dto.UserStatusDto;
import me.veso.attendanceservice.entity.Attendance;
import me.veso.attendanceservice.entity.CategoryId;
import me.veso.attendanceservice.entity.UserId;
import me.veso.attendanceservice.mapper.AttendanceMapper;
import me.veso.attendanceservice.repository.AttendanceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserIdService userIdService;
    private final CategoryIdService categoryService;
    private final RabbitClient rabbitClient;
    private final AttendanceMapper attendanceMapper;
    private final AttendanceService self;

    public List<AttendanceDetailsDto> createAttendance(AttendanceCreationDto attendanceCreationDto) {
        List<AttendanceDetailsDto> attendancesDetailsDto = new ArrayList<>();

        for (UserStatusDto userStatus : attendanceCreationDto.usersStatuses()) {
            log.info("Creating attendance for user ID {} in category ID {}", userStatus.userId(), attendanceCreationDto.categoryId());

            UserId userId = userIdService.saveIdLongIfNotExists(userStatus.userId());
            CategoryId categoryId = categoryService.saveIdLongIfNotExists(attendanceCreationDto.categoryId());

            Attendance attendance = new Attendance()
                    .setStatus(userStatus.status())
                    .setUserId(userId)
                    .setCategoryId(categoryId);

            Attendance attendanceSaved = self.saveAttendance(attendance);
            log.info("Attendance created successfully for user ID {} in category ID {}", userStatus.userId(), attendanceCreationDto.categoryId());
            attendancesDetailsDto.add(attendanceMapper.toAttendanceDetailsDto(attendanceSaved));
        }
        rabbitClient.notifyAttendanceCreated(attendancesDetailsDto);

        return attendancesDetailsDto;
    }

    public List<AttendanceDetailsDto> getAttendanceForCategory(String categoryId) {
        log.info("Fetching attendance records for category ID {}", categoryId);

        List<AttendanceDetailsDto> attendanceDetails = self.findAllByCategoryId(categoryId)
                .stream()
                .map(attendanceMapper::toAttendanceDetailsDto)
                .toList();

        log.info("Found {} attendance records for category ID {}", attendanceDetails.size(), categoryId);
        return attendanceDetails;
    }

    public List<AttendanceDetailsDto> getAttendanceForUser(Long userId) {
        log.info("Fetching attendance records for user ID {}", userId);

        List<AttendanceDetailsDto> attendanceDetails = self.findAllByUserId(userId)
                .stream()
                .map(attendanceMapper::toAttendanceDetailsDto)
                .toList();

        log.info("Found {} attendance records for user ID {}", attendanceDetails.size(), userId);
        return attendanceDetails;
    }

    @Transactional
    public void deleteByCategoryId(String categoryId) {
        log.info("Deleting attendance records for category ID {}", categoryId);

        attendanceRepository.deleteAllByCategoryId(categoryId);
        log.info("Deleted category with ID {}", categoryId);

        self.evictAttendanceFromCache(categoryId);
    }

    @Transactional
    public Attendance saveAttendance(Attendance attendance) {
        Attendance savedAttendance = attendanceRepository.save(attendance);
        return self.pushAttendanceToCache(savedAttendance);
    }

    @Cacheable(value = "attendanceByCategoryId", key = "#categoryId")
    public List<Attendance> findAllByCategoryId(String categoryId) {
        return attendanceRepository.findAllByCategory_CategoryId(categoryId);
    }

    @Cacheable(value = "attendanceByUserId", key = "#userId")
    public List<Attendance> findAllByUserId(Long userId) {
        return attendanceRepository.findAllByUser_UserId(userId);
    }

    @CachePut(value = "attendanceById", key = "#attendance.id")
    public Attendance pushAttendanceToCache(Attendance attendance) {
        log.info("Pushing attendance with id {} i cache", attendance.getId());
        return attendance;
    }

    @Caching(evict = {
            @CacheEvict(value = "attendanceByCategoryId", key = "#categoryId"),
            @CacheEvict(value = "attendanceById", allEntries = true),
            @CacheEvict(value = "attendanceByUserId", allEntries = true)
    })
    public void evictAttendanceFromCache(String categoryId) {
        log.info("Attendance evicted from cache with category id {}", categoryId);
    }
}
