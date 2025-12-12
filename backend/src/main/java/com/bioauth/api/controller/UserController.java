package com.bioauth.api.controller;

import com.bioauth.api.dto.UserDTO;
import com.bioauth.api.model.ApiResponse;
import com.bioauth.api.model.User;
import com.bioauth.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserDTO userDTO = convertToDTO(user);
                return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", userDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "User not found", null, "USER_NOT_FOUND"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error retrieving user: " + e.getMessage(), null, "ERROR"));
        }
    }

    @PutMapping("/{id}/template")
    public ResponseEntity<ApiResponse<UserDTO>> updateBiometricTemplate(
            @PathVariable Long id,
            @RequestBody String encryptedTemplate) {
        try {
            User updatedUser = userService.updateBiometricTemplate(id, encryptedTemplate);
            UserDTO userDTO = convertToDTO(updatedUser);
            return ResponseEntity.ok(new ApiResponse<>(true, "Biometric template updated successfully", userDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null, "USER_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error updating template: " + e.getMessage(), null, "ERROR"));
        }
    }

    @PutMapping("/{id}/status/enable")
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

    @PutMapping("/{id}/status/disable")
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
