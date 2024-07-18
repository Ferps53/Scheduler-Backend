package com.scheduler.auth.endpoint;

import com.scheduler.auth.annotation.PublicSession;
import com.scheduler.auth.controller.AuthController;
import com.scheduler.auth.dto.NewUserCreatedDTO;
import com.scheduler.auth.dto.TokenDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/auth")
public class AuthEndpoint {

    @Inject
    AuthController authController;

    @GET
    @PublicSession
    @Path("sign-in")
    public Response signIn(
            @HeaderParam("Authorization") String authorization,
            @QueryParam("username") String username,
            @QueryParam("email") String email,
            @QueryParam("password") String password
    ) {
        final NewUserCreatedDTO userDTO = authController.createNewUser(
                authorization,
                username,
                email,
                password
        );
        return Response.ok(userDTO).build();
    }

    @GET
    @PublicSession
    @Path("login")
    public Response login(
            @HeaderParam("Authorization") String authorization,
            @QueryParam("usernameOrEmail") String usernameOrEmail,
            @QueryParam("password") String password
    ) {
        final TokenDTO token = authController.login(authorization, usernameOrEmail, password);
        return Response.ok(token).build();
    }

    @GET
    @PublicSession
    @Path("refresh")
    public Response refreshToken(
            @HeaderParam("Authorization") String authorization,
            @QueryParam("refresh_token") String refreshToken
    ) {
        final TokenDTO token = authController.refreshToken(authorization, refreshToken);
        return Response.ok(token).build();
    }
}