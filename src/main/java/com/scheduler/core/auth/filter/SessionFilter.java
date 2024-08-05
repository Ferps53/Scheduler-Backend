package com.scheduler.core.auth.filter;

import com.scheduler.core.auth.annotation.PublicSession;
import com.scheduler.core.exceptions.ExceptionDTO;
import com.scheduler.core.exceptions.exception.UnauthorizedException;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import java.time.Instant;
import java.util.Optional;

public class SessionFilter {

    @Inject
    JWTParser jwtParser;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ServerRequestFilter(priority = 1)
    public Optional<Response> filter(ContainerRequestContext requestContext, ResourceInfo info) {

        if (!info.getResourceMethod().isAnnotationPresent(PublicSession.class)) {

            return validateToken(requestContext);
        }
        return Optional.empty();
    }

    private Optional<Response> validateToken(ContainerRequestContext requestContext) {

        final String token = requestContext.getHeaderString("Authorization");

        if (token == null || token.isEmpty() || !token.toLowerCase().startsWith("bearer ")) {
            return abortRequest();
        }

        try {
            JsonWebToken jwt = jwtParser.parse(token.substring(7));

            final Instant now = Instant.now();
            if (!jwt.getIssuer().equals(issuer) || jwt.getExpirationTime() < now.getEpochSecond()) {
                return abortRequest();
            }

            final String isRefresh = jwt.getClaim("is_refresh");
            if (isRefresh == null || isRefresh.isEmpty() || isRefresh.equals("true")) {
                return abortRequest();

            }
        } catch (Exception e) {
            return abortRequest();
        }
        return Optional.empty();
    }

    private Optional<Response> abortRequest() {
        return Optional.of((Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(new ExceptionDTO(new UnauthorizedException()))
                .build()));
    }
}
