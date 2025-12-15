package edu.com.bookingsystem.services;

import edu.com.bookingsystem.config.JwtUtil;
import edu.com.bookingsystem.models.user.*;
import edu.com.bookingsystem.repos.JwtTokenRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private JwtUtil jwtUtil;
    private AuthService userAccountService;
    private JwtTokenRepo jwtTokenRepo;

    @Autowired
    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    @Lazy
    public void setUserAccountService(AuthService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Autowired
    @Lazy
    public void setJwtTokenRepo(JwtTokenRepo jwtTokenRepo) {
        this.jwtTokenRepo = jwtTokenRepo;
    }
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        UserAccount user = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User customUser) {
            user = customUser.getUser();
        } else if (principal instanceof DefaultOAuth2User defaultUser) {
            String email = defaultUser.getAttribute("email");
            String name = defaultUser.getAttribute("name");
            // Lookup or save UserAccount by email here
            user = userAccountService.findOrCreateByEmailGoogle(email, name);
        } else {
            throw new IllegalStateException("Unknown principal type: " + principal.getClass());
        }

        assert user != null;
        Set<Role> newrole = user.getRoles().stream()
                .peek(role -> role.setName(role.getName().replaceFirst("ROLE_", "")))
                .collect(Collectors.toSet());
        user.setRoles(newrole);
        UserDetails userDetails = CustomUserDetails.builder()
                .user(user)
                .build();

        Optional<JwtToken> optionalToken = jwtTokenRepo.getByEmail(userDetails.getUsername());

        JwtToken token = optionalToken.orElseGet(() -> {
            System.out.println("Refreshing token");
            return JwtToken.builder()
                    .email(userDetails.getUsername())
                    .build();
        });

        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        token.setRefreshToken(refreshToken);
        JwtToken saved = jwtTokenRepo.save(token);

        // return JWT as JSON "token": "..."
        response.setContentType("application/json");
        response.getWriter().write("""
            { "token": "%s", "refreshToken": "%s" }
        """.formatted(accessToken, refreshToken));
    }
}