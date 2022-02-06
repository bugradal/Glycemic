package com.works.glycemic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class AuditAwareConfig implements AuditorAware<String> {
    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditAwareConfig();
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return Optional.ofNullable(authentication.getName());
    }

    public List<String> getRoles(){
        List<String> list = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        authentication.getAuthorities().forEach((role)->list.add(role.getAuthority()));
        return list;
    }
}


