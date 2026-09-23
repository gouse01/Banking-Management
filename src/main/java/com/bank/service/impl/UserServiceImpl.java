package com.bank.service.impl;

import com.bank.dto.RegisterDto;
import com.bank.entity.User;
import com.bank.exception.DuplicateEmailException;
import com.bank.exception.UserNotFoundException;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import com.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new DuplicateEmailException(
                    "An account with email '" + registerDto.getEmail() + "' already exists.");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setAddress(registerDto.getAddress());

        User savedUser = userRepository.save(user);

        // Automatically create a bank account for the newly registered user.
        accountService.createAccountForUser(savedUser);

        return savedUser;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));
    }
}
