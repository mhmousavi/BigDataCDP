package com.dcp.iam.domain;


import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "companies")
public class Company {
    @PrimaryKey
    private String name;
    @Column(nullable = false)
    private String password;

    public AuthenticatedCompany authenticate(PasswordEncoder encoder, String rawPassword) {
        if (!encoder.matches(rawPassword, password))
            throw new RuntimeException("bad credentials");
        return new AuthenticatedCompany(name);
    }

}
