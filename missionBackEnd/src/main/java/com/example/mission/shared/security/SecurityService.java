package com.example.mission.shared.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SecurityService {

    public void verifierAuthentifie() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("Vous devez être authentifié");
        }
    }

    public void verifierRole(String... rolesAutorises) {
        verifierAuthentifie();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean autorise = auth.getAuthorities().stream()
                .anyMatch(a -> {
                    for (String role : rolesAutorises) {
                        if (Objects.equals(a.getAuthority(), "ROLE_" + role)) {
                            return true;
                        }
                    }
                    return false;
                });

        if (!autorise) {
            throw new AccessDeniedException(
                    "Accès refusé. Rôles autorisés: " + String.join(", ", rolesAutorises));
        }
    }


    public String getEmailConnecte() {
        verifierAuthentifie();
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean aLeRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> Objects.requireNonNull(a.getAuthority()).equals("ROLE_" + role));
    }
}
