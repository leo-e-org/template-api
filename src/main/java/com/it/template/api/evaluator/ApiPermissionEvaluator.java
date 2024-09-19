package com.it.template.api.evaluator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApiPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (Objects.isNull(authentication) || Objects.isNull(permission) || !(permission instanceof String))
            return false;

        return hasPrivilege(authentication, targetDomainObject.toString().toUpperCase(), permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (Objects.isNull(authentication) || Objects.isNull(permission) || !(permission instanceof String))
            return false;

        return validateToken(authentication);
    }

    private boolean hasPrivilege(Authentication authentication, String targetType, String permission) {
        for (GrantedAuthority authority : authentication.getAuthorities())
            return authority.getAuthority().startsWith(targetType) && authority.getAuthority().contains(permission);

        return false;
    }

    private boolean validateToken(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        return StringUtils.isNotBlank(token) && StringUtils.startsWith(token, "Bearer ");
    }
}
