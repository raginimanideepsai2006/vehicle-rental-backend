package com.vehiclerental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long vehicleId;
    private String vehicleName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
