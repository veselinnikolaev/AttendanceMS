package me.veso.apigateway.dto;

public record TokenResponse (
    boolean isValid,
    String username,
    String role
) {}
