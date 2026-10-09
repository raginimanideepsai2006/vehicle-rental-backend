package com.vehiclerental.controller;

import com.vehiclerental.dto.AdminDashboardDto;
import com.vehiclerental.dto.ApiResponse;
import com.vehiclerental.dto.BookingResponseDto;
import com.vehiclerental.dto.UserProfileDto;
import com.vehiclerental.entity.BookingStatus;
import com.vehiclerental.service.AdminService;
import com.vehiclerental.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboard() {
        AdminDashboardDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard statistics retrieved successfully", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getAllUsers() {
        List<UserProfileDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved successfully", users));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getAllBookings() {
        List<BookingResponseDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success("All bookings retrieved successfully", bookings));
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<ApiResponse<BookingResponseDto>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        BookingResponseDto updated = bookingService.updateBookingStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Booking status updated to " + status, updated));
    }
}
