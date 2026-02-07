package edu.com.bookingsystem.controllers;

import edu.com.bookingsystem.api.AuthApi;
import edu.com.bookingsystem.config.JwtUtil;
import edu.com.bookingsystem.dtos.user.JwtDTO;
import edu.com.bookingsystem.dtos.user.UserRequestDTO;
import edu.com.bookingsystem.dtos.user.UserResponseDTO;
import edu.com.bookingsystem.models.user.CustomUserDetails;
import edu.com.bookingsystem.models.user.JwtToken;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.JwtTokenRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import edu.com.bookingsystem.services.UserService;
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
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserAccountRepo userAccountRepo;
    private final JwtTokenRepo  jwtTokenRepo;

    @Override
    public ResponseEntity<UserResponseDTO> createUser(UserRequestDTO userRequestDTO, Principal principal) {
        return ResponseEntity.ok(userService.createNewUserLocale(userRequestDTO, principal.getName()));

    }

    @Override
    public ResponseEntity<UserResponseDTO> registerAdmin(String email, UUID orgId, Principal principal) {
        return ResponseEntity.ok(userService.registerAdmin(email, orgId, List.of("SUPER_ADMIN", "ADMIN"), principal.getName()));

    }

    @Override
    public ResponseEntity<UserResponseDTO> registerManager(String email, UUID orgId, Principal principal) {
        return ResponseEntity.ok(userService.registerManager(email, orgId, List.of("MANAGER"), principal.getName()));
    }

    //only for non-oauth2 login works
    @Override
    public ResponseEntity<?> login(String email, String password) {
        Authentication auth = null;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)); // here jwt token tries to trigger to loaduserbyname()
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        UserDetails user = userDetailsService.loadUserByUsername(auth.getName());
        Optional<JwtToken> optionalToken = jwtTokenRepo.getByEmail(user.getUsername());

        JwtToken token = optionalToken.orElseGet(() -> {
            System.out.println("Refreshing token");
            return JwtToken.builder()
                    .email(user.getUsername())
                    .build();
        });

        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        String accessToken = jwtUtil.generateAccessToken(user);

        token.setRefreshToken(refreshToken);
        JwtToken saved = jwtTokenRepo.save(token);

        return ResponseEntity.ok(
                JwtDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(saved.getRefreshToken())
                        .build()
        );
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
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        return ResponseEntity.ok(new JwtDTO(newAccessToken, refreshToken));
    }
}
