package com.creditwise.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        
        // Assuming the principal is a CustomUserDetails with a method to get the user's first name
        if (authentication.getPrincipal() instanceof com.creditwise.security.CustomUserDetails) {
            com.creditwise.security.CustomUserDetails userDetails = 
                (com.creditwise.security.CustomUserDetails) authentication.getPrincipal();
            return Optional.of(userDetails.getUser().getFirstName());
        }
        
        // Fallback to username if not CustomUserDetails
        return Optional.of(authentication.getName());
    }
}