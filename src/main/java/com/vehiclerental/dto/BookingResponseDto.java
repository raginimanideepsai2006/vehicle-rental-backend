package com.vehiclerental.dto;

import com.vehiclerental.entity.BookingStatus;
import com.vehiclerental.entity.PaymentMethod;
import com.vehiclerental.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;

    private Long vehicleId;
    private String vehicleName;
    private String vehicleNumber;
    private String vehicleType;
    private String brand;
    private String model;
    private String imageUrl;
    private Double pricePerDay;

    private LocalDate pickupDate;
    private LocalDate returnDate;
    private Integer numberOfDays;
    private Double totalAmount;
    private BookingStatus bookingStatus;
    private LocalDateTime createdAt;

    // Payment details
    private Long paymentId;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String transactionId;
}
