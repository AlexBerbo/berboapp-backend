package tech.alexberbo.berboapp.service;

import jakarta.mail.MessagingException;
import tech.alexberbo.berboapp.enumerator.VerificationType;
import tech.alexberbo.berboapp.exception.EmailException;
import tech.alexberbo.berboapp.model.Message;

public interface EmailService {
    void sendConfirmationEmail(String firstName, String email, String token) throws EmailException, MessagingException;
    void sendTwoFactorCode(String firstName, String email, String code);
    void sendPasswordResetEmail(String firstName, String email, String verificationUrl);
    void sendVerifyEmail(String email, String firstName, String verificationUrl, VerificationType verificationType);
    void sendReport(Message message, String email);
}
