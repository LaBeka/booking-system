package edu.com.bookingsystem.services;

import edu.com.bookingsystem.models.user.AuthProvider;
import edu.com.bookingsystem.models.user.CustomOAuth2User;
import edu.com.bookingsystem.models.user.Role;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.RoleRepo;
import edu.com.bookingsystem.repos.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserAccountRepo userAccountRepo;
    private final RoleRepo roleRepo;

    //This tells Spring: “This is the authenticated principal for the session.”
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");
        List<Role> roles = roleRepo.findAll();
        Set<Role> userRole = roles.stream()
                .filter(role -> role.getName().equalsIgnoreCase("user"))
                .collect(Collectors.toSet());

        UserAccount user = userAccountRepo.findByEmail(email)
                .orElseGet(() -> {
                    UserAccount u = UserAccount.builder()
                            .fullName(name)
                            .email(email)
                            .active(true)
                            .deprecated(false)
                            .authProvider(AuthProvider.GOOGLE)
                            .roles(userRole)
                            .build();
                    return userAccountRepo.save(u);
                });

        // Wrap local user as principal
        return new CustomOAuth2User(oAuth2User.getAttributes(), user);
    }
}
