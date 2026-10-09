package com.vehiclerental.controller;

import com.vehiclerental.dto.ApiResponse;
import com.vehiclerental.dto.VehicleDto;
import com.vehiclerental.entity.AvailabilityStatus;
import com.vehiclerental.entity.VehicleType;
import com.vehiclerental.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleDto>>> getAllVehicles(
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) AvailabilityStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword) {
        List<VehicleDto> vehicles = vehicleService.getAllVehicles(type, status, location, minPrice, maxPrice, keyword);
        return ResponseEntity.ok(ApiResponse.success("Vehicles retrieved successfully", vehicles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDto>> getVehicleById(@PathVariable Long id) {
        VehicleDto vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle retrieved successfully", vehicle));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDto>> createVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
        VehicleDto created = vehicleService.createVehicle(vehicleDto);
        return new ResponseEntity<>(ApiResponse.success("Vehicle created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDto>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDto vehicleDto) {
        VehicleDto updated = vehicleService.updateVehicle(id, vehicleDto);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully"));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleDto>> updateAvailability(
            @PathVariable Long id,
            @RequestParam AvailabilityStatus status) {
        VehicleDto updated = vehicleService.updateAvailability(id, status);
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", updated));
    }
}
