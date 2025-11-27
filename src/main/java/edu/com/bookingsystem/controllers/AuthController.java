package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.AuthApi;
import edu.com.bookingsystem.config.JwtUtil;
import edu.com.bookingsystem.dtos.user.JwtDTO;
import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.models.user.CustomUserDetails;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.JwtTokenRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import edu.com.bookingsystem.services.AuthService;
import edu.com.bookingsystem.services.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserAccountRepo userAccountRepo;
    private final JwtTokenRepo  jwtTokenRepo;



    @Override
    public ResponseEntity<UserResponseDTO> registerUser(UserRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(authService.register(dto, List.of("USER"), "admin2@school.com"));
    }

    @Override
    public ResponseEntity<UserResponseDTO> registerSuperAdmin(UserRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(authService.register(dto, List.of("ADMIN", "USER"), principal.getName()));

    }

    @Override
    public ResponseEntity<UserResponseDTO> registerAdmin(UserRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(authService.register(dto, List.of("SUPER_ADMIN", "ADMIN", "USER"), principal.getName()));

    }

    //only for non oauth2 login works
    @Override
    public ResponseEntity<String> createAuthToken(String email, String password) {
        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)); // here jwt token tries to trigger to loaduserbyname()
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        UserDetails user = userDetailsService.loadUserByUsername(auth.getName());

        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> refreshAuthToken(String refreshToken) {
        Claims claims;
        try{
            claims = jwtUtil.extractAllClaims(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Invalid refresh token");
        }
        String username = claims.getSubject();

        UserAccount user = userAccountRepo.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
        String userRefreshToken = jwtTokenRepo
                .getByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getRefreshToken();

        if (!refreshToken.equals(userRefreshToken)){
            return ResponseEntity.status(403).body("Invalid refresh token");
        }
        UserDetails userDetails = CustomUserDetails.builder().user(user).build();
        String newAccessToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtDTO(newAccessToken, refreshToken));
    }
}
