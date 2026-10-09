package com.vehiclerental.service;

import com.vehiclerental.dto.AdminDashboardDto;
import com.vehiclerental.dto.UserProfileDto;

import java.util.List;

public interface AdminService {
    AdminDashboardDto getDashboardStats();
    List<UserProfileDto> getAllUsers();
}
