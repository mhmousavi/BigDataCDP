package com.dcp.iam.infra;

import com.dcp.iam.app.IAMHandler;
import com.dcp.iam.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
public class IAMController {
    private final IAMHandler iamHandler;
    private final JwtService jwt;

    @PostMapping("/users")
    public void createUser(@RequestBody SignUpRequest request) {
        this.iamHandler.handle(request);
    }

    @PostMapping(value = "/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        AuthenticatedCompany authenticatedCompany = this.iamHandler.handle(request);
        return new LoginResponse(jwt.generateAccessToken(authenticatedCompany));
    }

    @PostMapping(value = "/event")
    public void event(@RequestBody EventRequest request, @AuthenticationPrincipal AuthenticatedCompany company) throws ExecutionException, InterruptedException {
        this.iamHandler.handle(request.withCompanyName(company.name()));
    }
}
