package auth.dto;

public record UserLoginCredentials(String usernameOrEmail, String password) {
}