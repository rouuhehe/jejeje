package com.dbp.legalcheck.listener;

import com.dbp.legalcheck.domain.email.EmailService;
import com.dbp.legalcheck.event.SignInEmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;


@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @EventListener
    @Async
    public void handleSignInEmailEvent(SignInEmailEvent event) {
        var user = event.getUser();

        Context context = new Context();
        context.setVariable("fullName", user.getFirstName() + " " + user.getLastName());
        context.setVariable("email", user.getEmail());

        emailService.sendSignInEmail(
                user.getEmail(),
                "¡Bienvenido a LegalCheck!",
                "sign-in-confirmation",
                context
        );
    }
}
