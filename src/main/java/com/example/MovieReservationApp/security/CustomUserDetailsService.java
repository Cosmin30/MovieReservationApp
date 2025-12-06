package com.example.MovieReservationApp.security;

import com.example.MovieReservationApp.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            System.out.println("🔍 [CUSTOM USER DETAILS SERVICE] Loading user by email: " + email);
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

            System.out.println("🔍 [CUSTOM USER DETAILS SERVICE] User found - ID: " + user.getId());
            System.out.println("🔍 [CUSTOM USER DETAILS SERVICE] User found - Role: " + (user.getRole() != null ? user.getRole().name() : "NULL"));
            
            if (user.getRole() == null) {
                System.out.println("❌ [CUSTOM USER DETAILS SERVICE] ERROR: User role is NULL!");
            }
            
            CustomUserDetails userDetails = new CustomUserDetails(user);
            System.out.println("🔍 [CUSTOM USER DETAILS SERVICE] UserDetails created");
            System.out.println("🔍 [CUSTOM USER DETAILS SERVICE] Calling getAuthorities()...");
            var authorities = userDetails.getAuthorities();
            System.out.println("🔍 [CUSTOM USER DETAILS SERVICE] Authorities retrieved: " + authorities);
            return userDetails;
        } catch (Exception e) {
            System.out.println("❌ [CUSTOM USER DETAILS SERVICE] ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
