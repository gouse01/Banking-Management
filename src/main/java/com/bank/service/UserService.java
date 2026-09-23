package com.bank.service;

import com.bank.dto.RegisterDto;
import com.bank.entity.User;

public interface UserService {

    /**
     * Registers a new user, creates their bank account,
     * and persists both in a single transaction.
     */
    User registerUser(RegisterDto registerDto);

    User findByEmail(String email);
}
