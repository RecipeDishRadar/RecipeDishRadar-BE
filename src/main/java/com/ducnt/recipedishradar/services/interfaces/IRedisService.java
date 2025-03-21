package com.ducnt.recipedishradar.services.interfaces;

public interface IRedisService {
    void saveOtp(String email, String otp, long duration);

    String getOtp(String email);

    void deleteOtp(String email);

    void saveInvalidToken(String invalidToken, String value, long duration);
    String getInvalidToken(String invalidToken);
}
