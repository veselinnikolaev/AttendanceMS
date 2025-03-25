package me.veso.attendanceservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import me.veso.attendanceservice.dto.AttendanceCreationDto;
import me.veso.attendanceservice.service.AttendanceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<?> createAttendance(@Valid @RequestBody AttendanceCreationDto attendanceCreationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createAttendance(attendanceCreationDto));
    }

    @GetMapping("/category/{categoryId}")
    @Validated
    public ResponseEntity<?> getAttendance(
            @NotBlank(message = "Category id is required") @PathVariable("categoryId") String categoryId,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(value = "size", required = false, defaultValue = "10") String sizeParam) {
        ResponseEntity<?> response;
        if ("all".equalsIgnoreCase(sizeParam)) {
            response = ResponseEntity.ok(attendanceService.getAttendanceForCategory(categoryId));
        } else {
            int size = Integer.parseInt(sizeParam);
            pageable = PageRequest.of(pageable.getPageNumber(), size);
            response = ResponseEntity.ok(attendanceService.getAttendanceForCategoryPageable(categoryId, pageable));
        }

        return response;
    }

    @GetMapping("/user/{userId}")
    @Validated
    public ResponseEntity<?> getAttendanceForUser(
            @Positive(message = "User id must be positive") @PathVariable("userId") Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(value = "size", required = false, defaultValue = "10") String sizeParam) {
        ResponseEntity<?> response;
        if ("all".equalsIgnoreCase(sizeParam)) {
            response = ResponseEntity.ok(attendanceService.getAttendanceForUser(userId));
        } else {
            int size = Integer.parseInt(sizeParam);
            pageable = PageRequest.of(pageable.getPageNumber(), size);
            response = ResponseEntity.ok(attendanceService.getAttendanceForUserPageable(userId, pageable));
        }

        return response;
    }
}
