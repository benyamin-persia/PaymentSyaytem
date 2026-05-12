package com.bank.dto;

public record LoginResponse(String accessToken, String tokenType) { // Immutable API contract for JSON login responses.

    public LoginResponse(String accessToken) { // Convenience ctor so controllers pass only the signed JWT string.
        this(accessToken, "Bearer"); // Default OAuth2-style token type for Authorization headers.
    }
}
