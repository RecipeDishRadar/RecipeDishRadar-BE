package com.ducnt.recipedishradar.services.thirdparty;

import com.ducnt.recipedishradar.services.interfaces.IRedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService implements com.ducnt.recipedishradar.services.interfaces.IEmailService {
    JavaMailSender javaMailSender;
    Random random = new Random();
    IRedisService redisService;

    @Override
    public void sendOtp(String toEmail) {
        String otp = String.format("%06d", random.nextInt(999999));
        String savedOtp = redisService.getOtp(toEmail);
        if(savedOtp != null) {
            redisService.deleteOtp(toEmail);
        }
        redisService.saveOtp(toEmail, otp, 5);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Your OTP Code");
        mailMessage.setText("Your OTP code is: " + otp + ". It is valid for 5 minutes.");
        javaMailSender.send(mailMessage);
    }
}
