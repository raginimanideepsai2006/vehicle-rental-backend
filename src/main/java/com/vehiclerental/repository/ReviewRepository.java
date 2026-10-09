package com.vehiclerental.repository;

import com.vehiclerental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);
    List<Review> findAllByOrderByCreatedAtDesc();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vehicle.id = :vehicleId")
    Double findAverageRatingByVehicleId(@Param("vehicleId") Long vehicleId);

    long countByVehicleId(Long vehicleId);
}
