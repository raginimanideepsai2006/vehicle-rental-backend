package com.vehiclerental.dto;

import com.vehiclerental.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private LocalDateTime createdAt;
    private Long totalBookings;
}
