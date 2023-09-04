package tech.alexberbo.berboapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static tech.alexberbo.berboapp.constant.email.EmailConstants.*;

    /**
        This is an Email Sending mechanism that is configured here with the various properties and settings
        such as host, port, email from which the mails will come from, protocol and so on
        JavaMailSender is used here to send emails with a gmail third party app password
    */
@Configuration
@RequiredArgsConstructor
public class EmailConfig {
    private final Environment env;
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(GMAIL_HOST);
        mailSender.setPort(DEFAULT_PORT);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(env.getProperty("NOTHING_TO_SEE_HERE"));

        Properties properties = mailSender.getJavaMailProperties();
        properties.put(MAIL_TRANSPORT_PROTOCOL, SMTP);
        properties.put(SMTP_AUTH, true);
        properties.put(STARTTLS_ENABLE, true);
        properties.put(MAIL_DEBUG, true);
        return mailSender;
    }
}
