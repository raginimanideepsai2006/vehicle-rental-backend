package com.vehiclerental.controller;

import com.vehiclerental.dto.ApiResponse;
import com.vehiclerental.dto.BookingRequest;
import com.vehiclerental.dto.BookingResponseDto;
import com.vehiclerental.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDto>> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BookingRequest request) {
        BookingResponseDto booking = bookingService.createBooking(userDetails.getUsername(), request);
        return new ResponseEntity<>(ApiResponse.success("Booking created successfully", booking), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<BookingResponseDto> bookings = bookingService.getMyBookings(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBookingById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        BookingResponseDto booking = bookingService.getBookingById(id, userDetails.getUsername(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Booking details retrieved successfully", booking));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDto>> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        BookingResponseDto booking = bookingService.cancelBooking(id, userDetails.getUsername(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getBookingsByVehicle(@PathVariable Long vehicleId) {
        List<BookingResponseDto> bookings = bookingService.getBookingsByVehicle(vehicleId);
        return ResponseEntity.ok(ApiResponse.success("Vehicle bookings retrieved successfully", bookings));
    }

    @GetMapping("/check-availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @RequestParam Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        boolean available = bookingService.checkAvailability(vehicleId, pickupDate, returnDate);
        return ResponseEntity.ok(ApiResponse.success(
                available ? "Vehicle is available for the selected dates" : "Vehicle is not available for the selected dates",
                available
        ));
    }
}
