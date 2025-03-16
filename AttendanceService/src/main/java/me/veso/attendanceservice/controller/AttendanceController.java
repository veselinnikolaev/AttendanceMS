package me.veso.attendanceservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    public ResponseEntity<List<AttendanceDetailsDto>> createAttendance(@Valid @RequestBody AttendanceCreationDto attendanceCreationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createAttendance(attendanceCreationDto));
    }

    @GetMapping("/category/{categoryId}")
    @Validated
    public ResponseEntity<List<AttendanceDetailsDto>> getAttendance(
            @NotBlank(message = "Category id is required") @PathVariable("categoryId") String categoryId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForCategory(categoryId));
    }

    @GetMapping("/user/{userId}")
    @Validated
    public ResponseEntity<List<AttendanceDetailsDto>> getAttendanceForUser(
            @Positive(message = "User id must be positive") @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForUser(userId));
    }
}
