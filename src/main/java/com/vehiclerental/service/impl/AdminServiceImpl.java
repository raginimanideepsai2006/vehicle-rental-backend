package com.vehiclerental.service.impl;

import com.vehiclerental.dto.AdminDashboardDto;
import com.vehiclerental.dto.BookingResponseDto;
import com.vehiclerental.dto.UserProfileDto;
import com.vehiclerental.dto.VehicleDto;
import com.vehiclerental.entity.AvailabilityStatus;
import com.vehiclerental.entity.BookingStatus;
import com.vehiclerental.entity.Role;
import com.vehiclerental.repository.BookingRepository;
import com.vehiclerental.repository.UserRepository;
import com.vehiclerental.repository.VehicleRepository;
import com.vehiclerental.service.AdminService;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final VehicleService vehicleService;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.countByAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        long pending = bookingRepository.countByBookingStatus(BookingStatus.PENDING);
        long confirmed = bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED);
        long activeBookings = pending + confirmed;

        long completedRentals = bookingRepository.countByBookingStatus(BookingStatus.COMPLETED);
        Double totalRevenue = bookingRepository.calculateTotalRevenue();

        List<BookingResponseDto> recentBookings = bookingService.getAllBookings().stream()
                .limit(8)
                .collect(Collectors.toList());

        List<VehicleDto> popularVehicles = vehicleService.getAllVehicles(null, null, null, null, null, null).stream()
                .limit(6)
                .collect(Collectors.toList());

        return AdminDashboardDto.builder()
                .totalUsers(totalUsers)
                .totalCustomers(totalCustomers)
                .totalVehicles(totalVehicles)
                .availableVehicles(availableVehicles)
                .activeBookings(activeBookings)
                .completedRentals(completedRentals)
                .totalRevenue(totalRevenue != null ? Math.round(totalRevenue * 100.0) / 100.0 : 0.0)
                .recentBookings(recentBookings)
                .popularVehicles(popularVehicles)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserProfileDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .address(user.getAddress())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .totalBookings((long) user.getBookings().size())
                        .build())
                .collect(Collectors.toList());
    }
}
