package com.vehiclerental.service.impl;

import com.vehiclerental.dto.VehicleDto;
import com.vehiclerental.entity.AvailabilityStatus;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.entity.VehicleType;
import com.vehiclerental.exception.BookingException;
import com.vehiclerental.exception.ResourceNotFoundException;
import com.vehiclerental.repository.ReviewRepository;
import com.vehiclerental.repository.VehicleRepository;
import com.vehiclerental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VehicleDto> getAllVehicles(VehicleType type, AvailabilityStatus status, String location,
                                           Double minPrice, Double maxPrice, String keyword) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanLocation = (location != null && !location.trim().isEmpty()) ? location.trim() : null;

        List<Vehicle> vehicles = vehicleRepository.filterVehicles(type, status, cleanLocation, minPrice, maxPrice, cleanKeyword);
        return vehicles.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
        return mapToDto(vehicle);
    }

    @Override
    @Transactional
    public VehicleDto createVehicle(VehicleDto dto) {
        if (vehicleRepository.findByVehicleNumber(dto.getVehicleNumber()).isPresent()) {
            throw new BookingException("Vehicle with registration number already exists: " + dto.getVehicleNumber());
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleName(dto.getVehicleName())
                .vehicleNumber(dto.getVehicleNumber().toUpperCase())
                .vehicleType(dto.getVehicleType())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .fuelType(dto.getFuelType())
                .transmission(dto.getTransmission())
                .seatingCapacity(dto.getSeatingCapacity())
                .pricePerDay(dto.getPricePerDay())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .availabilityStatus(dto.getAvailabilityStatus() != null ? dto.getAvailabilityStatus() : AvailabilityStatus.AVAILABLE)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public VehicleDto updateVehicle(Long id, VehicleDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        // Check unique vehicle number
        if (!vehicle.getVehicleNumber().equalsIgnoreCase(dto.getVehicleNumber())) {
            if (vehicleRepository.findByVehicleNumber(dto.getVehicleNumber()).isPresent()) {
                throw new BookingException("Vehicle with registration number already exists: " + dto.getVehicleNumber());
            }
            vehicle.setVehicleNumber(dto.getVehicleNumber().toUpperCase());
        }

        vehicle.setVehicleName(dto.getVehicleName());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setSeatingCapacity(dto.getSeatingCapacity());
        vehicle.setPricePerDay(dto.getPricePerDay());
        vehicle.setLocation(dto.getLocation());
        vehicle.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            vehicle.setImageUrl(dto.getImageUrl());
        }
        if (dto.getAvailabilityStatus() != null) {
            vehicle.setAvailabilityStatus(dto.getAvailabilityStatus());
        }

        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
        vehicleRepository.delete(vehicle);
    }

    @Override
    @Transactional
    public VehicleDto updateAvailability(Long id, AvailabilityStatus status) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
        vehicle.setAvailabilityStatus(status);
        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToDto(updated);
    }

    private VehicleDto mapToDto(Vehicle vehicle) {
        Double avgRating = reviewRepository.findAverageRatingByVehicleId(vehicle.getId());
        long totalReviews = reviewRepository.countByVehicleId(vehicle.getId());

        return VehicleDto.builder()
                .id(vehicle.getId())
                .vehicleName(vehicle.getVehicleName())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .fuelType(vehicle.getFuelType())
                .transmission(vehicle.getTransmission())
                .seatingCapacity(vehicle.getSeatingCapacity())
                .pricePerDay(vehicle.getPricePerDay())
                .location(vehicle.getLocation())
                .description(vehicle.getDescription())
                .imageUrl(vehicle.getImageUrl())
                .availabilityStatus(vehicle.getAvailabilityStatus())
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0)
                .totalReviews(totalReviews)
                .build();
    }
}
