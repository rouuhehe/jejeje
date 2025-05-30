package com.dbp.legalcheck.domain.email;

import com.dbp.legalcheck.domain.user.User;
import com.dbp.legalcheck.event.SignInEmailEvent;
import com.dbp.legalcheck.event.WelcomeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

@Service
@RequiredArgsConstructor
public class EmailApplicationService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void sendSignInEmail(User user) {
        applicationEventPublisher.publishEvent(new SignInEmailEvent(user));
    }

    public void sendWelcomeEmail(User user) {
        applicationEventPublisher.publishEvent(new WelcomeEvent(user));
    }
}
