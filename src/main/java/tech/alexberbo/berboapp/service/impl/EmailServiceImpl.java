package tech.alexberbo.berboapp.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tech.alexberbo.berboapp.service.EmailService;

import java.util.Date;
import java.util.concurrent.CancellationException;

import static tech.alexberbo.berboapp.constant.email.EmailConstants.*;

/**
    This is the pattern of sending emails, one is for confirming the email before login, and the other is for the MFA verification code.
    Using the JavaMailSender we set the settings and properties of each email before we send it and use it in other implementations of the app.
 */
@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private JavaMailSender mailSender;
    @Override
    @Async
    public void sendConfirmationEmail(String firstName, String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, UTF_8);
            helper.setSubject(SUBJECT_WELCOME);
            helper.setTo(email);
            helper.setText("Hello " +
                    firstName +
                    ". Please confirm your email by clicking on this link: " +
                    "http://localhost:8080/reddit/confirm/" + token);
            helper.setSentDate(new Date());
            mailSender.send(message);
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            throw new CancellationException("Error, can't send Email to: " + email);
        }
    }

    @Override
    public void sendTwoFactorCode(String firstName, String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, UTF_8);
            helper.setSubject(SUBJECT_TWO_FACTOR);
            helper.setTo(email);
            helper.setText("Hello " +
                    firstName +
                    ". Your Two factor authentication code: " + code);
            helper.setSentDate(new Date());
            mailSender.send(message);
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            throw new CancellationException("Error, can't send Email to: " + email);
        }
    }

    @Override
    public void sendPasswordResetEmail(String firstName, String email, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, UTF_8);
            helper.setSubject(SUBJECT_PASSWORD_RESET);
            helper.setTo(email);
            helper.setText("Hello " + firstName + " your verification url for password recovery is: " + verificationUrl);
            helper.setSentDate(new Date());
            mailSender.send(message);
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            throw new CancellationException("Error, can't send Email to: " + email);
        }
    }

    @Override
    public void sendVerifyAccountEmail(String email, String firstName, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, UTF_8);
            helper.setSubject(SUBJECT_ACCOUNT_VERIFICATION);
            helper.setTo(email);
            helper.setText("Hello " + firstName + " please verify your account by clicking this link: " + verificationUrl);
            helper.setSentDate(new Date());
            mailSender.send(message);
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            throw new CancellationException("Error, can't send Email to: " + email);
        }
    }
}
