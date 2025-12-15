package edu.com.bookingsystem.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping(GoogleApi.API_PATH_DICTIONARY)
@Tag(name = "Methods to work with GOOGLE AUTHENTICATION", description = GoogleApi.API_PATH_DICTIONARY)
@Validated
public interface GoogleApi {
    String API_PATH_DICTIONARY = "/";

    @GetMapping()
    @Operation(summary = "Registration/Login new user with USER role via google account 'localhost:8200'")
    void redirectToGoogleLogin(HttpServletResponse response) throws IOException;
}
