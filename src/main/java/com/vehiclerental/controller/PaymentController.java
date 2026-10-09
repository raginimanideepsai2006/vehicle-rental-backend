package com.vehiclerental.controller;

import com.vehiclerental.dto.ApiResponse;
import com.vehiclerental.dto.PaymentRequest;
import com.vehiclerental.dto.PaymentResponseDto;
import com.vehiclerental.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDto>> processPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponseDto payment = paymentService.processPayment(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", payment));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentByBookingId(@PathVariable Long bookingId) {
        PaymentResponseDto payment = paymentService.getPaymentByBookingId(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Payment details retrieved successfully", payment));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getAllPayments() {
        List<PaymentResponseDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.success("All payments retrieved successfully", payments));
    }
}
