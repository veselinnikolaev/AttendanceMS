package me.veso.attendanceservice.dto;

import java.util.List;

public record CategoryDetailsDto(
        String id,
        String name,
        Long checkerId,
        List<Long> attendantsIds
) {}
