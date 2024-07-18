package com.scheduler.auth.dto;

public record UserLoginCredentials(String usernameOrEmail, String password) {
}