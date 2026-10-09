package com.vehiclerental.repository;

import com.vehiclerental.entity.AvailabilityStatus;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
    List<Vehicle> findByAvailabilityStatus(AvailabilityStatus status);
    List<Vehicle> findByVehicleType(VehicleType vehicleType);
    long countByAvailabilityStatus(AvailabilityStatus status);

    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:type IS NULL OR v.vehicleType = :type) AND " +
           "(:status IS NULL OR v.availabilityStatus = :status) AND " +
           "(:location IS NULL OR LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minPrice IS NULL OR v.pricePerDay >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.pricePerDay <= :maxPrice) AND " +
           "(:keyword IS NULL OR (LOWER(v.vehicleName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(v.model) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    List<Vehicle> filterVehicles(@Param("type") VehicleType type,
                                 @Param("status") AvailabilityStatus status,
                                 @Param("location") String location,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice,
                                 @Param("keyword") String keyword);
}
