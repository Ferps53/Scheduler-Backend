package auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenDTO(
        @JsonProperty("acess_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("type") String tokenType
) {
}