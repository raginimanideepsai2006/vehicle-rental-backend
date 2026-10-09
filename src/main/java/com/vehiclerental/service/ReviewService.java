package com.vehiclerental.service;

import com.vehiclerental.dto.ReviewRequest;
import com.vehiclerental.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto addReview(String userEmail, ReviewRequest request);
    List<ReviewResponseDto> getReviewsByVehicle(Long vehicleId);
    List<ReviewResponseDto> getAllReviews();
    void deleteReview(Long id);
}
