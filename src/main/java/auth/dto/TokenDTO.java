package auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenDTO(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("type") String tokenType
) {

    public TokenDTO (String accessToken, String refreshToken) {
      this (accessToken, refreshToken, 3600, "bearer");
    }
}