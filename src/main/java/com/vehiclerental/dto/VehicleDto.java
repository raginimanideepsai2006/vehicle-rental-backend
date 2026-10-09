package com.vehiclerental.dto;

import com.vehiclerental.entity.AvailabilityStatus;
import com.vehiclerental.entity.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    private Long id;

    @NotBlank(message = "Vehicle name is required")
    private String vehicleName;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @NotBlank(message = "Transmission is required")
    private String transmission;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    private Integer seatingCapacity;

    @NotNull(message = "Price per day is required")
    @Min(value = 1, message = "Price per day must be positive")
    private Double pricePerDay;

    @NotBlank(message = "Location is required")
    private String location;

    private String description;

    private String imageUrl;

    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    private Double averageRating;

    private Long totalReviews;
}
