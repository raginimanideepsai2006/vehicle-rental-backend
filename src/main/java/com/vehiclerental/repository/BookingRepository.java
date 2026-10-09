package com.vehiclerental.repository;

import com.vehiclerental.entity.Booking;
import com.vehiclerental.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Booking> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByBookingStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId " +
           "AND b.bookingStatus IN ('CONFIRMED', 'PENDING') " +
           "AND (:pickupDate <= b.returnDate AND :returnDate >= b.pickupDate)")
    List<Booking> findOverlappingBookings(@Param("vehicleId") Long vehicleId,
                                           @Param("pickupDate") LocalDate pickupDate,
                                           @Param("returnDate") LocalDate returnDate);

    boolean existsByUserIdAndVehicleIdAndBookingStatus(Long userId, Long vehicleId, BookingStatus bookingStatus);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0.0) FROM Booking b WHERE b.bookingStatus = 'COMPLETED'")
    Double calculateTotalRevenue();

    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findRecentBookings();
}
