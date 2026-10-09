package com.vehiclerental.controller;

import com.vehiclerental.dto.ApiResponse;
import com.vehiclerental.dto.ReviewRequest;
import com.vehiclerental.dto.ReviewResponseDto;
import com.vehiclerental.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> addReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponseDto review = reviewService.addReview(userDetails.getUsername(), request);
        return new ResponseEntity<>(ApiResponse.success("Review submitted successfully", review), HttpStatus.CREATED);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<List<ReviewResponseDto>>> getReviewsByVehicle(@PathVariable Long vehicleId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByVehicle(vehicleId);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved successfully", reviews));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewResponseDto>>> getAllReviews() {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(ApiResponse.success("All reviews retrieved successfully", reviews));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }
}
