package ch.pfaditools.accounting.security;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return (UserEntity) authentication.getPrincipal();
        }
        return null; // User is not logged in
    }

    public static String getAuthenticatedUsername() {
        return Optional.ofNullable(getCurrentUser()).map(UserEntity::getUsername).orElse(null);
    }

    public static GroupEntity getAuthenticatedUserGroup() {
        return Optional.ofNullable(getCurrentUser()).map(UserEntity::getGroup).orElse(null);
    }

    public static boolean isUserInRole(String role) {
        return getCurrentUser().getRoles().contains(role);
    }
}
