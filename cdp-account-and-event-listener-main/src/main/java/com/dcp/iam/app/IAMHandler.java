package com.dcp.iam.app;

import com.dcp.iam.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class IAMHandler {
    private final CompaniesRepository companiesRepository;
    private final PasswordEncoder encoder;
    private final KafkaTemplate<String, Event> kafkaTemplate;
    private final KafkaAdmin adminClient;

    @Transactional
    public void handle(SignUpRequest request) {

        if (Boolean.TRUE.equals(companiesRepository.existsById(request.getName()))) {
            throw new RuntimeException();
        } else {
            var user = new Company(request.getName(), this.encoder.encode(request.getPassword()));
            this.companiesRepository.save(user);
            adminClient.createOrModifyTopics(
                    TopicBuilder.name(request.getName())
                            .build()
            );
        }
    }


    public AuthenticatedCompany handle(LoginRequest request) {
        var user = this.companiesRepository.findById(request.getName()).orElseThrow();
        return user.authenticate(encoder, request.getPassword());
    }

    public void handle(EventRequest request) throws ExecutionException, InterruptedException {
        kafkaTemplate.send(
                request.getCompanyName(),
                new Event(
                        request.getId(),
                        request.getTimestamp(),
                        request.getType(),
                        request.getData()
                )
        ).get();
    }

}
