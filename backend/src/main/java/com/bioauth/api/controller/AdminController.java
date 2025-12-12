package com.bioauth.api.controller;

import com.bioauth.api.dto.AuthLogDTO;
import com.bioauth.api.dto.UserDTO;
import com.bioauth.api.model.ApiResponse;
import com.bioauth.api.model.User;
import com.bioauth.api.model.User.UserRole;
import com.bioauth.api.service.AuthLogService;
import com.bioauth.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthLogService authLogService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Users retrieved successfully",
                    users
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error retrieving users: " + e.getMessage(), null, "ERROR"));
        }
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            List<UserDTO> users = userService.getUsersByRole(userRole);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Users retrieved successfully",
                    users
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Invalid role: " + role, null, "BAD_REQUEST"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error retrieving users: " + e.getMessage(), null, "ERROR"));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<AuthLogDTO>>> getAllAuthLogs() {
        try {
            List<AuthLogDTO> logs = authLogService.getAllAuthLogs();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Authentication logs retrieved successfully",
                    logs
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error retrieving logs: " + e.getMessage(), null, "ERROR"));
        }
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<ApiResponse<List<AuthLogDTO>>> getUserAuthLogs(@PathVariable Long userId) {
        try {
            List<AuthLogDTO> logs = authLogService.getUserAuthLogs(userId);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "User authentication logs retrieved successfully",
                    logs
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error retrieving logs: " + e.getMessage(), null, "ERROR"));
        }
    }

    @PutMapping("/user/{id}/status/enable")
    public ResponseEntity<ApiResponse<UserDTO>> enableUser(@PathVariable Long id) {
        try {
            User updatedUser = userService.enableUser(id);
            UserDTO userDTO = convertToDTO(updatedUser);
            return ResponseEntity.ok(new ApiResponse<>(true, "User enabled successfully", userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "USER_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error enabling user: " + e.getMessage(), null, "ERROR"));
        }
    }

    @PutMapping("/user/{id}/status/disable")
    public ResponseEntity<ApiResponse<UserDTO>> disableUser(@PathVariable Long id) {
        try {
            User updatedUser = userService.disableUser(id);
            UserDTO userDTO = convertToDTO(updatedUser);
            return ResponseEntity.ok(new ApiResponse<>(true, "User disabled successfully", userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "USER_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error disabling user: " + e.getMessage(), null, "ERROR"));
        }
    }

    @PutMapping("/user/{id}/status/lock")
    public ResponseEntity<ApiResponse<UserDTO>> lockUser(@PathVariable Long id) {
        try {
            User updatedUser = userService.lockUser(id);
            UserDTO userDTO = convertToDTO(updatedUser);
            return ResponseEntity.ok(new ApiResponse<>(true, "User locked successfully", userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "USER_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error locking user: " + e.getMessage(), null, "ERROR"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getSystemStats() {
        try {
            List<UserDTO> allUsers = userService.getAllUsers();
            List<AuthLogDTO> allLogs = authLogService.getAllAuthLogs();

            Object stats = new Object() {
                public final int totalUsers = allUsers.size();
                public final int totalAuthAttempts = allLogs.size();
                public final long activeUsers = allUsers.stream()
                        .filter(u -> "ACTIVE".equals(u.getStatus())).count();
            };

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "System statistics retrieved successfully",
                    stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error retrieving stats: " + e.getMessage(), null, "ERROR"));
        }
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole().toString());
        dto.setStatus(user.getStatus().toString());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setBiometricTemplateUpdatedAt(user.getBiometricTemplateUpdatedAt());
        return dto;
    }
}
