package auth.endpoint;

import auth.controller.AuthController;
import auth.dto.NewUserCreatedDTO;
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
}
