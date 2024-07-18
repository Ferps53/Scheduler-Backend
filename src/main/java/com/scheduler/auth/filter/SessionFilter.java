package com.scheduler.auth.filter;

import com.scheduler.auth.annotation.PublicSession;
import com.scheduler.exceptions.ExceptionDTO;
import com.scheduler.exceptions.exception.UnauthorizedException;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;

@Provider
@Priority(1)
public class SessionFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo info;

    @Inject
    JWTParser jwtParser;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        if (!info.getResourceMethod().isAnnotationPresent(PublicSession.class)) {

            validateToken(requestContext);
        }
    }

    private void validateToken(ContainerRequestContext requestContext) {

        final String token = requestContext.getHeaderString("Authorization");

        if (token == null || token.isEmpty() || !token.toLowerCase().startsWith("bearer ")) {
            abortRequest(requestContext);
            return;
        }

        try {
            JsonWebToken jwt = jwtParser.parse(token.substring(7));

            final Instant now = Instant.now();
            if (!jwt.getIssuer().equals(issuer) || jwt.getExpirationTime() < now.getEpochSecond()) {
                abortRequest(requestContext);
                return;
            }

            final String isRefresh = jwt.getClaim("is_refresh");
            if (isRefresh == null || isRefresh.isEmpty() || isRefresh.equals("true")) {
                abortRequest(requestContext);
            }
        } catch (Exception e) {
            abortRequest(requestContext);
        }
    }

    private void abortRequest(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(new ExceptionDTO(new UnauthorizedException()))
                .build());
    }
}
