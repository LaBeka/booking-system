package edu.com.bookingsystem.services;

import edu.com.bookingsystem.mappers.UserMapper;
import edu.com.bookingsystem.repos.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountRepo userAccountRepo;
    private final UserMapper userMapper;

}
