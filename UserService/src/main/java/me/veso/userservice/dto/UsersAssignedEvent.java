package me.veso.userservice.dto;

import java.util.List;

public record UsersAssignedEvent(
    Long checkerId,
    List<Long> attendantsIds,
    String categoryId
) {}
