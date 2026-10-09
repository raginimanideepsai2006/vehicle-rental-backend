package com.vehiclerental.service;

import com.vehiclerental.dto.AuthRequest;
import com.vehiclerental.dto.AuthResponse;
import com.vehiclerental.dto.RegisterRequest;
import com.vehiclerental.dto.UserProfileDto;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    UserProfileDto getProfile(String email);
    UserProfileDto updateProfile(String email, RegisterRequest request);
}
