package me.veso.categoryservice.dto;

import java.util.List;

public record UsersAssignedEvent (
    Long checkerId,
    List<Long> attendantsIds,
    String categoryId
) {}
