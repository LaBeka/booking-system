package edu.com.bookingsystem.services;

import edu.com.bookingsystem.models.user.CustomUserDetails;
import edu.com.bookingsystem.models.user.UserAccount;
import edu.com.bookingsystem.repos.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        user.getRoles().stream().forEach(System.out::println);
        //use these 2 lines of code for the users who has raw password "pass"
        PasswordEncoder encoder =  new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        return new CustomUserDetails(user);
    }
}