package com.dbp.legalcheck.listener;

import com.dbp.legalcheck.domain.email.EmailService;
import com.dbp.legalcheck.event.SignInEmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.dbp.legalcheck.domain.user.User


@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @EventListener
    @Async
    public void handleSignInEmailEvent(SignInEmailEvent event) {
        var user = event.getUser();
        emailService.sendSignInEmail(user.getEmail(), user.getFirstName() + " " + user.getLastName());
    }


}
