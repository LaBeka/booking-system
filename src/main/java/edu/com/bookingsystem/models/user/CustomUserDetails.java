package edu.com.bookingsystem.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserAccount user;
    // 2. The Cached Fields (Fast access)
    private final UUID id;
    private final UUID organizationId;
    private final String fullName;

    public CustomUserDetails(UserAccount user) {
        this.user = user;
        // Auto-extract the data immediately
        this.id = user.getId();
        this.fullName = user.getFullName();
        // Handle potential null Organization safely
        if (user.getOrganization() != null) {
            this.organizationId = user.getOrganization().getId();
        } else {
            this.organizationId = null;
        }
    }

//    public UserAccount getUser() {
//        return user;
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role ->new SimpleGrantedAuthority(role.getName().replaceFirst("^ROLE_", "")))
                .toList();
    }

    @Override
    public String getPassword() {
        if(user.getAuthProvider().equals(AuthProvider.LOCAL)){
            return user.getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
