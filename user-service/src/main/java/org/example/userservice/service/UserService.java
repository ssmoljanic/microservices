package org.example.userservice.service;

import org.example.userservice.dto.UpdateProfileRequest;
import org.example.userservice.dto.UserResponse;
import org.example.userservice.dto.session.UpdateStatsRequest;
import org.example.userservice.dto.session.UserStatusResponse;

public interface UserService {

    UserResponse getMe(Long userId);

    UserResponse updateMe(Long userId, UpdateProfileRequest request);

    UserStatusResponse getUserStatus(Long userId);

    void updateStats(Long userId, UpdateStatsRequest request);

    String getUserEmail(Long userId);

}
