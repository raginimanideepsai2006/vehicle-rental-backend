package com.vehiclerental.service.impl;

import com.vehiclerental.dto.ReviewRequest;
import com.vehiclerental.dto.ReviewResponseDto;
import com.vehiclerental.entity.BookingStatus;
import com.vehiclerental.entity.Review;
import com.vehiclerental.entity.User;
import com.vehiclerental.entity.Vehicle;
import com.vehiclerental.exception.BookingException;
import com.vehiclerental.exception.ResourceNotFoundException;
import com.vehiclerental.repository.BookingRepository;
import com.vehiclerental.repository.ReviewRepository;
import com.vehiclerental.repository.UserRepository;
import com.vehiclerental.repository.VehicleRepository;
import com.vehiclerental.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ReviewResponseDto addReview(String userEmail, ReviewRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        // Check if customer has completed rental
        boolean hasCompletedRental = bookingRepository.existsByUserIdAndVehicleIdAndBookingStatus(
                user.getId(),
                vehicle.getId(),
                BookingStatus.COMPLETED
        );

        if (!hasCompletedRental) {
            throw new BookingException("Review can only be submitted after completing a rental with this vehicle.");
        }

        Review review = Review.builder()
                .user(user)
                .vehicle(vehicle)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToDto(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByVehicle(Long vehicleId) {
        return reviewRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));
        reviewRepository.delete(review);
    }

    private ReviewResponseDto mapToDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .vehicleId(review.getVehicle().getId())
                .vehicleName(review.getVehicle().getVehicleName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
