package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.GoogleApi;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/*
* Important that in my Google Cloud Console -> APIs & Services -> Credentials
* - > OAuth 2.0 Client IDs - > Authorized redirect URIs TO HAVE - >
* - > http://localhost:{SERVER.PORT}/login/oauth2/code/google
 */
@RestController
public class RootRedirectController implements GoogleApi {

    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}

