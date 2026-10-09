package com.vehiclerental.service.impl;

import com.vehiclerental.dto.PaymentRequest;
import com.vehiclerental.dto.PaymentResponseDto;
import com.vehiclerental.entity.Booking;
import com.vehiclerental.entity.BookingStatus;
import com.vehiclerental.entity.Payment;
import com.vehiclerental.entity.PaymentStatus;
import com.vehiclerental.exception.BookingException;
import com.vehiclerental.exception.ResourceNotFoundException;
import com.vehiclerental.exception.UnauthorizedException;
import com.vehiclerental.repository.BookingRepository;
import com.vehiclerental.repository.PaymentRepository;
import com.vehiclerental.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public PaymentResponseDto processPayment(String userEmail, PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

        if (!booking.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new UnauthorizedException("You are not authorized to make a payment for this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED ||
            booking.getBookingStatus() == BookingStatus.REJECTED) {
            throw new BookingException("Cannot process payment for a " + booking.getBookingStatus() + " booking.");
        }

        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElse(null);

        if (payment != null && payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new BookingException("Payment has already been completed for this booking.");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (payment == null) {
            payment = Payment.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .transactionId(transactionId)
                    .paymentDate(LocalDateTime.now())
                    .build();
        } else {
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transactionId);
            payment.setPaymentDate(LocalDateTime.now());
        }

        Payment savedPayment = paymentRepository.save(payment);

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return mapToDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking ID: " + bookingId));
        return mapToDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PaymentResponseDto mapToDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
