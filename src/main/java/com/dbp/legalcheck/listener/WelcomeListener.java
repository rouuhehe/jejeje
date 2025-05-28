package com.dbp.legalcheck.listener;

import com.dbp.legalcheck.domain.email.EmailService;
import com.dbp.legalcheck.event.WelcomeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;


@Component
@RequiredArgsConstructor
public class WelcomeListener {

    private final EmailService emailService;

    @EventListener
    @Async
    public void handleWelcomeEvent(WelcomeEvent event) {
        var user = event.getUser();

        Context context = new Context();
        context.setVariable("fullName", user.getFirstName() + " " + user.getLastName());
        context.setVariable("email", user.getEmail());

        emailService.sendEmail(
                user.getEmail(),
                "Verificacion",
                "welcome.html",
                context
        );
    }
}