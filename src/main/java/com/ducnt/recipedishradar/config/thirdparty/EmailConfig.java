package com.ducnt.recipedishradar.config.thirdparty;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailConfig {
    @Value("${spring.mail.host}")
    String SPRING_MAIL_HOST;

    @Value("${spring.mail.port}")
    String SPRING_MAIL_PORT;

    @Value("${spring.mail.username}")
    String SPRING_MAIL_USERNAME;

    @Value("${spring.mail.password}")
    String SPRING_MAIL_PASSWORD;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost(SPRING_MAIL_HOST);
        javaMailSenderImpl.setPort(Integer.parseInt(SPRING_MAIL_PORT));
        javaMailSenderImpl.setUsername(SPRING_MAIL_USERNAME);
        javaMailSenderImpl.setPassword(SPRING_MAIL_PASSWORD);

        Properties javaMailProperties = javaMailSenderImpl.getJavaMailProperties();
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.ssl,trust", "smtp.gmail.com");

        return javaMailSenderImpl;
    }
}
