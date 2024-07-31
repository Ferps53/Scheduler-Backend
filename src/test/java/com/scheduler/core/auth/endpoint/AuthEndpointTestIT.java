package com.scheduler.core.auth.endpoint;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;

import java.util.Base64;

import static io.restassured.RestAssured.given;

@QuarkusTest
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthEndpointTestIT {

    @ConfigProperty(name = "basic.username")
    String basicUsername;

    @ConfigProperty(name = "basic.password")
    String basicPassword;

    @Test
    @Order(1)
    void loginOk() {
        given()
                .header("Authorization", "Basic " + generateBasic())
                .param("usernameOrEmail", "test")
                .param("password", "12345678")
                .get("/auth/login")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(1)
    void login401Basic() {
        given()
                .header("Authorization", "Basic 123")
                .param("usernameOrEmail", "test")
                .param("password", "12345678")
                .get("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(1)
    void loginUserNotSignedIn() {
        given()
                .header("Authorization", "Basic " + generateBasic())
                .param("usernameOrEmail", "notFound")
                .param("password", "11111")
                .get("/auth/login")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(2)
    void signIn() {
        given()
                .header("Authorization", "Basic " + generateBasic())
                .param("username", "test2")
                .param("email", "test2@gmail.com")
                .param("password", "12345678")
                .when()
                .get("/auth/sign-in")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(3)
    void loginWithNewUser() {
        given()
                .header("Authorization", "Basic " + generateBasic())
                .param("usernameOrEmail", "test2")
                .param("password", "12345678")
                .get("/auth/login")
                .then()
                .statusCode(200);
    }

    String generateBasic() {
        return Base64.getEncoder().encodeToString((basicUsername + ":" + basicPassword).getBytes());
    }
}
