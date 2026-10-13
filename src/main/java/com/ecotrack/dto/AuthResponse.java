package com.ecotrack.dto;

public record AuthResponse(String token, String tipo, String email, String role) {}
