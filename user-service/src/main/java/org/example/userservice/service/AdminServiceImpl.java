package org.example.userservice.service;

import org.example.userservice.domain.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void blockUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(true);
        userRepository.save(user);

    }

    @Override
    public void unblockUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(false);
        userRepository.save(user);

    }
}
