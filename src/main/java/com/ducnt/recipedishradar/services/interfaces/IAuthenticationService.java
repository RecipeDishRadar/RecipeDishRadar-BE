package com.ducnt.recipedishradar.services.interfaces;

import com.ducnt.recipedishradar.dto.request.authentication.*;
import com.ducnt.recipedishradar.dto.response.authentication.AuthenticationResponse;
import com.ducnt.recipedishradar.exception.CustomException;
import com.ducnt.recipedishradar.exception.ErrorResponse;
import com.ducnt.recipedishradar.models.Account;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public interface IAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    boolean register(RegisterRequest request);

    boolean verifyAccountByOtp(VerifyOtpRequest request);

    void logout(LogoutRequest request);

    AuthenticationResponse refreshToken(RefreshRequest request);

    boolean introspect(String token) throws JOSEException, ParseException;

}
