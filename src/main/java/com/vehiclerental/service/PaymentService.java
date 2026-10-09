package com.vehiclerental.service;

import com.vehiclerental.dto.PaymentRequest;
import com.vehiclerental.dto.PaymentResponseDto;

import java.util.List;

public interface PaymentService {
    PaymentResponseDto processPayment(String userEmail, PaymentRequest request);
    PaymentResponseDto getPaymentByBookingId(Long bookingId);
    List<PaymentResponseDto> getAllPayments();
}
