package me.veso.attendanceservice.controller;

import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.dto.AttendanceCreationDto;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceDetailsDto createAttendance(@RequestBody AttendanceCreationDto attendanceCreationDto) {
        return attendanceService.createAttendance(attendanceCreationDto);
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AttendanceDetailsDto> getAttendance(@PathVariable Long categoryId) {
        return attendanceService.getAttendanceForCategory(categoryId);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<AttendanceDetailsDto> getAttendanceForUser(@PathVariable Long userId) {
        return attendanceService.getAttendanceForUser(userId);
    }
}
