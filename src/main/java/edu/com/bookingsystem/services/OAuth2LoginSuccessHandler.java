package edu.com.bookingsystem.services;

import com.sun.jdi.PrimitiveValue;
import edu.com.bookingsystem.config.JwtUtil;
import edu.com.bookingsystem.models.user.CustomOAuth2User;
import edu.com.bookingsystem.models.user.CustomUserDetails;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.UserAccountRepo;
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
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private JwtUtil jwtUtil;
    private AuthService userAccountService;

    @Autowired
    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    @Lazy
    public void setUserAccountService(AuthService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        UserAccount user = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User customUser) {
            user = customUser.getUser();
        } else if (principal instanceof DefaultOAuth2User defaultUser) {
            String email = defaultUser.getAttribute("email");
            String name = defaultUser.getAttribute("name");
            // Lookup or save UserAccount by email here
            user = userAccountService.findOrCreateByEmail(email, name);
        } else if (principal instanceof UserDetails userDetails) {
            // Optional: handle UserDetails principal
            // This can be used in non-OAuth flows
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
        String token = jwtUtil.generateToken(userDetails);

        // Example: return JWT as JSON
        response.setContentType("application/json");
        response.getWriter().write("""
            { "token": "%s" }
        """.formatted(token));
    }
}