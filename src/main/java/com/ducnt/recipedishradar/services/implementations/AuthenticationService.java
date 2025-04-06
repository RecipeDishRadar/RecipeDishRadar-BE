package com.ducnt.recipedishradar.services.implementations;

import com.ducnt.recipedishradar.dto.request.authentication.*;
import com.ducnt.recipedishradar.dto.response.authentication.AuthenticationResponse;
import com.ducnt.recipedishradar.enums.AccountRole;
import com.ducnt.recipedishradar.enums.AccountStatus;
import com.ducnt.recipedishradar.exception.CustomException;
import com.ducnt.recipedishradar.exception.ErrorResponse;
import com.ducnt.recipedishradar.exception.ExistedException;
import com.ducnt.recipedishradar.exception.NotFoundException;
import com.ducnt.recipedishradar.models.Account;
import com.ducnt.recipedishradar.repositories.AccountRepository;
import com.ducnt.recipedishradar.services.interfaces.IEmailService;
import com.ducnt.recipedishradar.services.interfaces.IRedisService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService implements com.ducnt.recipedishradar.services.interfaces.IAuthenticationService {
    AccountRepository accountRepository;
    ModelMapper modelMapper;
    IRedisService redisService;
    IEmailService emailService;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.jwt-signature-key}")
    protected String JWT_SIGNATURE_KEY;

    @NonFinal
    @Value("${jwt.accessible-duration}")
    protected Long ACCESSIBLE_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${jwt.accessible-duration-type}")
    protected String ACCESSIBLE_DURATION_TYPE;

    @NonFinal
    @Value("${jwt.refreshable-duration-type}")
    protected String REFRESHABLE_DURATION_TYPE;

    @NonFinal
    @Value("${spring.application.name}")
    protected String ISSUER;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (account.getStatus().equals(AccountStatus.NOT_VERIFIED)) {
            throw new CustomException(ErrorResponse.ACCOUNT_IS_NOT_VERIFIED);
        }
        if (account.getStatus().equals(AccountStatus.INACTIVE)) {
            throw new CustomException(ErrorResponse.ACCOUNT_IS_INACTIVE);
        }

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if (!isAuthenticated) {
            throw new CustomException(ErrorResponse.UNAUTHENTICATED);
        }
        var accessToken = generateAccessToken(account);
        var refreshToken = generateRefreshToken(account);
        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {
        try {
            var signToken = verifyToken(request.getRefreshToken());

            var account = accountRepository.findByEmail(signToken.getJWTClaimsSet().getSubject())
                    .orElseThrow(() -> new NotFoundException("Email is not found"));
            var accessToken = generateAccessToken(account);

            return AuthenticationResponse
                    .builder()
                    .accessToken(accessToken)
                    .build();
        } catch (Exception ex) {
            throw new CustomException(ErrorResponse.INTERNAL_SERVER);
        }
    }

    public String generateAccessToken(Account account) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject(account.getEmail())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now()
                                .plus(ACCESSIBLE_DURATION, ChronoUnit.valueOf(ACCESSIBLE_DURATION_TYPE))
                                .toEpochMilli()
                ))
                .claim("scope", account.getRole())
                .build();

        return getTokenString(jwsHeader, jwtClaimsSet);
    }

    public String generateRefreshToken(Account account) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("recipedishradar.com")
                .subject(account.getEmail())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now()
                                .plus(REFRESHABLE_DURATION, ChronoUnit.valueOf(REFRESHABLE_DURATION_TYPE))
                                .toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .build();

        return getTokenString(jwsHeader, jwtClaimsSet);
    }

    private String getTokenString(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet) {
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(JWT_SIGNATURE_KEY.getBytes()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    @Transactional
    public  boolean register(RegisterRequest request) {
        try {
            if (accountRepository.findByEmail(request.getEmail()).isPresent())
                throw new ExistedException("Email is existed");

            Account account = modelMapper.map(request, Account.class);

            account.setPassword(passwordEncoder.encode(request.getPassword()));
            account.setRole(AccountRole.USER);
            account.setStatus(AccountStatus.NOT_VERIFIED);

            accountRepository.save(account);

            emailService.sendOtp(account.getEmail());

            return true;
        } catch (ExistedException e) {
            throw new ExistedException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorResponse.INTERNAL_SERVER);
        }
    }

    @Override
    @Transactional
    public boolean verifyAccountByOtp(VerifyOtpRequest request) {
        try {
            String email = request.getEmail();
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Email is not found"));
            String savedOtp = redisService.getOtp(email);
            if(savedOtp != null && savedOtp.equals(request.getOtp())) {
                redisService.deleteOtp(email);
                account.setStatus(AccountStatus.ACTIVE);
                accountRepository.save(account);
                return true;
            }
            return false;
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new CustomException(ErrorResponse.INTERNAL_SERVER);
        }
    }

    @Override
    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyToken(request.getRefreshToken());
            var jti = signToken.getJWTClaimsSet().getJWTID();
            var uid = signToken.getJWTClaimsSet().getClaim("uid");
            StringBuilder invalidationToken = new StringBuilder("TOKEN_INVALID");
            invalidationToken.append("_").append(uid).append("_").append(jti);

            redisService.saveInvalidToken(invalidationToken.toString(), "true", 20);
        } catch (Exception ex) {
            throw new CustomException(ErrorResponse.INTERNAL_SERVER);
        }
    }

    @Override
    public boolean introspect(String token) throws JOSEException, ParseException {
        try {
            verifyToken(token);
            return true;
        } catch(Exception ex) {
            return false;
        }

    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(JWT_SIGNATURE_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        var uid = signedJWT.getJWTClaimsSet().getClaim("uid");
        StringBuilder invalidationToken = new StringBuilder("TOKEN_INVALID");
        invalidationToken.append("_").append(uid).append("_").append(jti);

        if (!(signedJWT.verify(verifier) && expirationTime.after(new Date())))
            throw new CustomException(ErrorResponse.UNAUTHENTICATED);

        if (jti != null && redisService.getInvalidToken(invalidationToken.toString()) != null)
            throw new CustomException(ErrorResponse.UNAUTHENTICATED);

        return signedJWT;
    }


}
