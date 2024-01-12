package tech.alexberbo.berboapp.constant.email;

public interface EmailConstants {
    String GMAIL_HOST = "smtp.gmail.com";
    int DEFAULT_PORT = 587;
    String USERNAME = "alexberbo1997@gmail.com";
    String ADMIN = "berbo997@gmail.com";
    String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    String SMTP = "smtp";
    String SMTP_AUTH = "mail.smtp.auth";
    String STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    String MAIL_DEBUG = "mail.debug";
    String SUBJECT = "Your new password for your new account";
    String SUBJECT_WELCOME = "Welcome to Berboapp!";
    String SUBJECT_TWO_FACTOR = "Two factor authentication code!";
    String SUBJECT_PASSWORD_RESET = "Verification URL for password recovery";
    String SUBJECT_ACCOUNT_VERIFICATION = "Verify your account!";
    String SUBJECT_REPORT = "Report came in!";
    String UTF_8 = "utf-8";
    String ERROR = "Error: ";
    String ERROR_SENDING_EMAIL = "Failed to send email, please try again! ";
    String EMAIL_SENT = "Email with a new password is sent to: ";
}
