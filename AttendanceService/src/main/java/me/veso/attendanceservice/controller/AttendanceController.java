package me.veso.attendanceservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.dto.AttendanceCreationDto;
import me.veso.attendanceservice.dto.AttendanceDetailsDto;
import me.veso.attendanceservice.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceDetailsDto> createAttendance(@Valid @RequestBody AttendanceCreationDto attendanceCreationDto) {
        //TODO: Notify for new attendance
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createAttendance(attendanceCreationDto));
    }

    @GetMapping("/category/{categoryId}")
    @Validated
    public ResponseEntity<List<AttendanceDetailsDto>> getAttendance(@Positive(message = "Category id must be positive") @PathVariable Long categoryId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForCategory(categoryId));
    }

    @GetMapping("/user/{userId}")
    @Validated
    public ResponseEntity<List<AttendanceDetailsDto>> getAttendanceForUser(@Positive(message = "User id must be positive") @PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForUser(userId));
    }
}
