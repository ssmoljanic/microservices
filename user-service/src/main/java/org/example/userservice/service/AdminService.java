package org.example.userservice.service;

public interface AdminService {

    void blockUser(Long userId);
    void unblockUser(Long userId);

}
