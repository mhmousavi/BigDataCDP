package com.dcp.middlewares;


import com.dcp.iam.domain.AuthenticatedCompany;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

public class JwtAuth implements Authentication {
    private final AuthenticatedCompany authenticatedCompany;
    private boolean authenticated;

    public JwtAuth(AuthenticatedCompany authenticatedCompany) {
        this.authenticatedCompany = authenticatedCompany;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.authenticatedCompany;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        this.authenticated = false;
    }

    @Override
    public String getName() {
        return this.authenticatedCompany.name();
    }
}
