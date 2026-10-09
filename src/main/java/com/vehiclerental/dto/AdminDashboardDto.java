package com.vehiclerental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private long totalUsers;
    private long totalCustomers;
    private long totalVehicles;
    private long availableVehicles;
    private long activeBookings; // PENDING + CONFIRMED
    private long completedRentals;
    private Double totalRevenue;
    private List<BookingResponseDto> recentBookings;
    private List<VehicleDto> popularVehicles;
}
