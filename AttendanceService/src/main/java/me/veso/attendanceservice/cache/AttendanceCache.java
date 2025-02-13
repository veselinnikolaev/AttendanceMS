package me.veso.attendanceservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.entity.Attendance;
import me.veso.attendanceservice.repository.AttendanceRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttendanceCache {
    private final AttendanceRepository attendanceRepository;
    private final CacheManager cacheManager;

    @PostConstruct
    public void preloadCache() {
        //attendanceByCategoryId, attendanceById, attendanceByUserId
        List<Attendance> allAttendance = attendanceRepository.findAll();
        if (allAttendance.isEmpty()) {
            return;
        }

        Cache attendanceByIdCache = cacheManager.getCache("attendanceById");
        Cache attendanceByCategoryIdCache = cacheManager.getCache("attendanceByCategoryId");
        Cache attendanceByUserIdCache = cacheManager.getCache("attendanceByUserId");

        if (attendanceByIdCache != null) {
            attendanceByIdCache.clear();
            allAttendance.forEach(attendance -> attendanceByIdCache.put(attendance.getId(), attendance));
        }

        if (attendanceByCategoryIdCache != null) {
            attendanceByCategoryIdCache.clear();
            allAttendance.forEach(attendance -> attendanceByCategoryIdCache.put(attendance.getCategory().getCategoryId(), attendance));
        }


        if (attendanceByUserIdCache != null) {
            attendanceByUserIdCache.clear();
            allAttendance.forEach(attendance -> attendanceByUserIdCache.put(attendance.getUser().getUserId(), attendance));
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshCache(){
        preloadCache();
    }
}
