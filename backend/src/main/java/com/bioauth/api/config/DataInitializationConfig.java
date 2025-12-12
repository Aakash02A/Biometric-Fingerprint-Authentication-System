package com.bioauth.api.config;

import com.bioauth.api.model.User;
import com.bioauth.api.model.User.UserRole;
import com.bioauth.api.model.User.UserStatus;
import com.bioauth.api.repository.UserRepository;
import com.bioauth.api.utils.AESEncryptionUtil;
import com.bioauth.api.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class DataInitializationConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Check if data already exists
            Optional<User> existingUser = userRepository.findByUsername("admin");
            if (existingUser.isPresent()) {
                System.out.println("Database already initialized with sample data");
                return;
            }

            try {
                // Create Admin User
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@bioauth.com");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setPasswordHash(SecurityUtil.hashPassword("admin123"));
                admin.setRole(UserRole.ADMIN);
                admin.setStatus(UserStatus.ACTIVE);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                
                // Create sample biometric template for admin
                String adminTemplate = "ADMIN_FINGERPRINT_TEMPLATE_DATA_FOR_TESTING";
                admin.setEncryptedBiometricTemplate(AESEncryptionUtil.encrypt(adminTemplate));
                
                userRepository.save(admin);
                System.out.println("Created admin user: admin / admin123");

                // Create Sample User 1
                User user1 = new User();
                user1.setUsername("alice");
                user1.setEmail("alice@bioauth.com");
                user1.setFirstName("Alice");
                user1.setLastName("Johnson");
                user1.setPasswordHash(SecurityUtil.hashPassword("alice123"));
                user1.setRole(UserRole.USER);
                user1.setStatus(UserStatus.ACTIVE);
                user1.setCreatedAt(LocalDateTime.now());
                user1.setUpdatedAt(LocalDateTime.now());
                
                String alice_template = "ALICE_FINGERPRINT_TEMPLATE_DATA_FOR_TESTING_12345678";
                user1.setEncryptedBiometricTemplate(AESEncryptionUtil.encrypt(alice_template));
                
                userRepository.save(user1);
                System.out.println("Created user: alice / alice123");

                // Create Sample User 2
                User user2 = new User();
                user2.setUsername("bob");
                user2.setEmail("bob@bioauth.com");
                user2.setFirstName("Bob");
                user2.setLastName("Smith");
                user2.setPasswordHash(SecurityUtil.hashPassword("bob123"));
                user2.setRole(UserRole.USER);
                user2.setStatus(UserStatus.ACTIVE);
                user2.setCreatedAt(LocalDateTime.now());
                user2.setUpdatedAt(LocalDateTime.now());
                
                String bob_template = "BOB_FINGERPRINT_TEMPLATE_DATA_FOR_TESTING_87654321";
                user2.setEncryptedBiometricTemplate(AESEncryptionUtil.encrypt(bob_template));
                
                userRepository.save(user2);
                System.out.println("Created user: bob / bob123");

                // Create Sample User 3
                User user3 = new User();
                user3.setUsername("carol");
                user3.setEmail("carol@bioauth.com");
                user3.setFirstName("Carol");
                user3.setLastName("White");
                user3.setPasswordHash(SecurityUtil.hashPassword("carol123"));
                user3.setRole(UserRole.USER);
                user3.setStatus(UserStatus.ACTIVE);
                user3.setCreatedAt(LocalDateTime.now());
                user3.setUpdatedAt(LocalDateTime.now());
                
                String carol_template = "CAROL_FINGERPRINT_TEMPLATE_DATA_FOR_TESTING_11223344";
                user3.setEncryptedBiometricTemplate(AESEncryptionUtil.encrypt(carol_template));
                
                userRepository.save(user3);
                System.out.println("Created user: carol / carol123");

                // Create Sample User 4 (Inactive)
                User user4 = new User();
                user4.setUsername("david");
                user4.setEmail("david@bioauth.com");
                user4.setFirstName("David");
                user4.setLastName("Brown");
                user4.setPasswordHash(SecurityUtil.hashPassword("david123"));
                user4.setRole(UserRole.USER);
                user4.setStatus(UserStatus.INACTIVE);
                user4.setCreatedAt(LocalDateTime.now());
                user4.setUpdatedAt(LocalDateTime.now());
                
                String david_template = "DAVID_FINGERPRINT_TEMPLATE_DATA_FOR_TESTING_55667788";
                user4.setEncryptedBiometricTemplate(AESEncryptionUtil.encrypt(david_template));
                
                userRepository.save(user4);
                System.out.println("Created user: david / david123 (INACTIVE)");

                System.out.println("\n==========================================");
                System.out.println("Sample Data Initialization Complete");
                System.out.println("==========================================");
                System.out.println("Admin Credentials: admin / admin123");
                System.out.println("Test Users: alice, bob, carol, david (all with password 'username123')");
                System.out.println("==========================================\n");

            } catch (Exception e) {
                System.err.println("Error initializing data: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
