package com.dcp.iam.domain;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    public static final String TOKEN_TYPE_CLAIM = "token_type";

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiry.minutes}")
    private int tokenExpiryMinutes;

    @Autowired
    private Algorithm algorithm;


    public String generateAccessToken(AuthenticatedCompany company) {
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofMinutes(this.tokenExpiryMinutes)));
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(company.name())
                .withClaim(TOKEN_TYPE_CLAIM, "access_token")
                .withExpiresAt(expiresAt)
                .sign(this.algorithm);
    }


    public AuthenticatedCompany decodeAccessToken(String accessToken) {
        JWTVerifier verifier = JWT.require(this.algorithm)
                .withIssuer(issuer)
                .withClaim(TOKEN_TYPE_CLAIM, "access_token")
                .build();

        var decodedJWT = verifier.verify(accessToken);
        var name = decodedJWT.getSubject();

        return new AuthenticatedCompany(name);
    }
}
