package com.vehiclerental.service;

import com.vehiclerental.dto.VehicleDto;
import com.vehiclerental.entity.AvailabilityStatus;
import com.vehiclerental.entity.VehicleType;

import java.util.List;

public interface VehicleService {
    List<VehicleDto> getAllVehicles(VehicleType type, AvailabilityStatus status, String location,
                                    Double minPrice, Double maxPrice, String keyword);
    VehicleDto getVehicleById(Long id);
    VehicleDto createVehicle(VehicleDto vehicleDto);
    VehicleDto updateVehicle(Long id, VehicleDto vehicleDto);
    void deleteVehicle(Long id);
    VehicleDto updateAvailability(Long id, AvailabilityStatus status);
}
