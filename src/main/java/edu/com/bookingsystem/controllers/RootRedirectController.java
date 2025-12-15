package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.GoogleApi;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RootRedirectController implements GoogleApi {

    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}

