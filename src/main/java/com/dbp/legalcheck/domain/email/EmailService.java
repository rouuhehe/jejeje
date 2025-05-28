package com.dbp.legalcheck.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendSignInEmail(String to, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Bienvenido a LegalCheck!");

            String content = """
                    <html>
                    <body>
                        <h1>¡Hola %s!</h1>
                        <p>Gracias por registrarte en LegalCheck.</p>
                        <p>Estamos felices de tenerte como parte de nuestra comunidad.</p>
                    </body>
                    </html>
                    """.formatted(fullName);

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("No se pudo enviar el email", e);
        }
    }
}
