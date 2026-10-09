package com.vehiclerental.service;

import com.vehiclerental.dto.BookingRequest;
import com.vehiclerental.dto.BookingResponseDto;
import com.vehiclerental.entity.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(String userEmail, BookingRequest request);
    List<BookingResponseDto> getMyBookings(String userEmail);
    BookingResponseDto getBookingById(Long id, String userEmail, boolean isAdmin);
    BookingResponseDto cancelBooking(Long id, String userEmail, boolean isAdmin);
    List<BookingResponseDto> getBookingsByVehicle(Long vehicleId);
    List<BookingResponseDto> getAllBookings();
    BookingResponseDto updateBookingStatus(Long id, BookingStatus status);
    boolean checkAvailability(Long vehicleId, LocalDate pickupDate, LocalDate returnDate);
}
