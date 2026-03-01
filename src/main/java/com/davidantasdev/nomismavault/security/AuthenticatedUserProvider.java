package com.davidantasdev.nomismavault.security;

import com.davidantasdev.nomismavault.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        return (User) authentication.getPrincipal();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public void validateOwnership(Long resourceUserId) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(resourceUserId)) {
            throw new AccessDeniedException("Access denied: You don't have permission to access this resource");
        }
    }

    public boolean isOwner(Long resourceUserId) {
        return getCurrentUserId().equals(resourceUserId);
    }
}
