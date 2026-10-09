package com.vehiclerental.service.impl;

import com.vehiclerental.dto.BookingRequest;
import com.vehiclerental.dto.BookingResponseDto;
import com.vehiclerental.entity.*;
import com.vehiclerental.exception.BookingException;
import com.vehiclerental.exception.ResourceNotFoundException;
import com.vehiclerental.exception.UnauthorizedException;
import com.vehiclerental.repository.BookingRepository;
import com.vehiclerental.repository.PaymentRepository;
import com.vehiclerental.repository.UserRepository;
import com.vehiclerental.repository.VehicleRepository;
import com.vehiclerental.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(String userEmail, BookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        if (vehicle.getAvailabilityStatus() == AvailabilityStatus.MAINTENANCE) {
            throw new BookingException("This vehicle is currently undergoing maintenance and cannot be booked.");
        }

        LocalDate today = LocalDate.now();
        if (request.getPickupDate().isBefore(today)) {
            throw new BookingException("Pickup date cannot be in the past.");
        }

        if (request.getReturnDate().isBefore(request.getPickupDate())) {
            throw new BookingException("Return date cannot be before pickup date.");
        }

        // Overlap check
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                vehicle.getId(),
                request.getPickupDate(),
                request.getReturnDate()
        );

        if (!overlapping.isEmpty()) {
            Booking conflict = overlapping.get(0);
            throw new BookingException("Vehicle is not available for the selected dates. " +
                    "A conflicting booking exists from " + conflict.getPickupDate() + " to " + conflict.getReturnDate());
        }

        // Calculate days and total amount
        long daysBetween = ChronoUnit.DAYS.between(request.getPickupDate(), request.getReturnDate());
        int numberOfDays = (int) (daysBetween <= 0 ? 1 : daysBetween);
        double totalAmount = numberOfDays * vehicle.getPricePerDay();

        BookingStatus initialStatus = (request.getPaymentMethod() != null)
                ? BookingStatus.CONFIRMED
                : BookingStatus.PENDING;

        Booking booking = Booking.builder()
                .user(user)
                .vehicle(vehicle)
                .pickupDate(request.getPickupDate())
                .returnDate(request.getReturnDate())
                .numberOfDays(numberOfDays)
                .totalAmount(totalAmount)
                .bookingStatus(initialStatus)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Process payment if method provided
        if (request.getPaymentMethod() != null) {
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Payment payment = Payment.builder()
                    .booking(savedBooking)
                    .amount(totalAmount)
                    .paymentMethod(request.getPaymentMethod())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId(transactionId)
                    .build();
            paymentRepository.save(payment);
            savedBooking.setPayment(payment);
        }

        return mapToDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id, String userEmail, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (!isAdmin && !booking.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to view this booking.");
        }

        return mapToDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(Long id, String userEmail, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (!isAdmin && !booking.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to cancel this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED ||
            booking.getBookingStatus() == BookingStatus.COMPLETED ||
            booking.getBookingStatus() == BookingStatus.REJECTED) {
            throw new BookingException("Booking cannot be cancelled because it is already " + booking.getBookingStatus());
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        // If payment was made, set to REFUNDED
        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElse(null);
        if (payment != null && payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }

        Booking updated = bookingRepository.save(booking);
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByVehicle(Long vehicleId) {
        return bookingRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        booking.setBookingStatus(status);
        Booking updated = bookingRepository.save(booking);
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long vehicleId, LocalDate pickupDate, LocalDate returnDate) {
        List<Booking> conflicts = bookingRepository.findOverlappingBookings(vehicleId, pickupDate, returnDate);
        return conflicts.isEmpty();
    }

    private BookingResponseDto mapToDto(Booking booking) {
        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElse(null);

        return BookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .userEmail(booking.getUser().getEmail())
                .userPhone(booking.getUser().getPhone())
                .vehicleId(booking.getVehicle().getId())
                .vehicleName(booking.getVehicle().getVehicleName())
                .vehicleNumber(booking.getVehicle().getVehicleNumber())
                .vehicleType(booking.getVehicle().getVehicleType().name())
                .brand(booking.getVehicle().getBrand())
                .model(booking.getVehicle().getModel())
                .imageUrl(booking.getVehicle().getImageUrl())
                .pricePerDay(booking.getVehicle().getPricePerDay())
                .pickupDate(booking.getPickupDate())
                .returnDate(booking.getReturnDate())
                .numberOfDays(booking.getNumberOfDays())
                .totalAmount(booking.getTotalAmount())
                .bookingStatus(booking.getBookingStatus())
                .createdAt(booking.getCreatedAt())
                .paymentId(payment != null ? payment.getId() : null)
                .paymentStatus(payment != null ? payment.getPaymentStatus() : PaymentStatus.PENDING)
                .paymentMethod(payment != null ? payment.getPaymentMethod() : null)
                .transactionId(payment != null ? payment.getTransactionId() : null)
                .build();
    }
}
