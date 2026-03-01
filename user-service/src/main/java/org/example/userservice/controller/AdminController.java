package org.example.userservice.controller;

import org.example.userservice.security.CheckSecurity;
import org.example.userservice.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PatchMapping("/{id}/block")
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<?> block(@PathVariable Long id,
                                   @RequestHeader("Authorization") String authorization) {
        adminService.blockUser(id);
        return ResponseEntity.ok(Map.of("blocked", true));
    }

    @PatchMapping("/{id}/unblock")
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<?> unblock(@PathVariable Long id,
                                     @RequestHeader("Authorization") String authorization) {
        adminService.unblockUser(id);
        return ResponseEntity.ok(Map.of("blocked", false));
    }
}
