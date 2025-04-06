package services;

import com.ducnt.recipedishradar.dto.request.authentication.AuthenticationRequest;
import com.ducnt.recipedishradar.dto.response.authentication.AuthenticationResponse;
import com.ducnt.recipedishradar.enums.AccountStatus;
import com.ducnt.recipedishradar.exception.CustomException;
import com.ducnt.recipedishradar.exception.ErrorResponse;
import com.ducnt.recipedishradar.exception.NotFoundException;
import com.ducnt.recipedishradar.models.Account;
import com.ducnt.recipedishradar.repositories.AccountRepository;
import com.ducnt.recipedishradar.services.implementations.AuthenticationService;
import com.ducnt.recipedishradar.services.interfaces.IAuthenticationService;
import com.ducnt.recipedishradar.services.interfaces.IEmailService;
import com.ducnt.recipedishradar.services.interfaces.IRedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    IRedisService redisService;
    @Mock
    IEmailService emailService;
    @InjectMocks
    AuthenticationService authenticationService;
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void authenticate_withValidCredentials_returnsAuthenticationResponse() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "password123");
        Account account = Account
                .builder()
                .email("user@example.com")
                .password("$2a$10$hashedpassword")
                .status(AccountStatus.ACTIVE)
                .build();

        doReturn(Optional.of(account)).when(accountRepository).findByEmail("user@example.com");
        doReturn(true).when(passwordEncoder).matches("password123", "$2a$10$hashedpassword");

        AuthenticationService spyAuthService = spy(authenticationService);
        doReturn("mocked-access-token").when(spyAuthService).generateAccessToken(account);
        doReturn("mocked-refresh-token").when(spyAuthService).generateRefreshToken(account);
        // Act
        AuthenticationResponse response = spyAuthService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-access-token", response.getAccessToken());
        assertEquals("mocked-refresh-token", response.getRefreshToken());
    }

    @Test
    void authenticate_withNonExistentAccount_throwsNotFoundException() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("unknown@example.com", "password123");
        doReturn(Optional.empty()).when(accountRepository).findByEmail("unknown@example.com");

        // Act & Assert
        AuthenticationService spyAuthService = spy(authenticationService);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            spyAuthService.authenticate(request);
        });
        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void authenticate_withNotVerifiedAccount_throwsCustomException() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "password123");
        Account account = Account
                .builder()
                .email("user@example.com")
                .password("$2a$10$hashedpassword")
                .status(AccountStatus.NOT_VERIFIED)
                .build();
        doReturn(Optional.of(account)).when(accountRepository).findByEmail("user@example.com");

        // Act & Assert
        AuthenticationService spyAuthService = spy(authenticationService);
        CustomException exception = assertThrows(CustomException.class, () -> {
            spyAuthService.authenticate(request);
        });
        assertEquals(ErrorResponse.ACCOUNT_IS_NOT_VERIFIED, exception.getErrorResponse());
    }

    @Test
    void authenticate_withInactiveAccount_throwsCustomException() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "password123");
        Account account = Account
                .builder()
                .email("user@example.com")
                .password("$2a$10$hashedpassword")
                .status(AccountStatus.INACTIVE)
                .build();
        doReturn(Optional.of(account)).when(accountRepository).findByEmail("user@example.com");

        // Act & Assert
        AuthenticationService spyAuthService = spy(authenticationService);
        CustomException exception = assertThrows(CustomException.class, () -> {
            spyAuthService.authenticate(request);
        });
        assertEquals(ErrorResponse.ACCOUNT_IS_INACTIVE, exception.getErrorResponse());
    }

    @Test
    void authenticate_withWrongPassword_throwsCustomException() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("user@example.com", "wrongpassword");
        Account account = Account
                .builder()
                .email("user@example.com")
                .password("$2a$10$hashedpassword")
                .status(AccountStatus.ACTIVE)
                .build();
        doReturn(Optional.of(account)).when(accountRepository).findByEmail("user@example.com");
        doReturn(false).when(passwordEncoder).matches("wrongpassword", "$2a$10$hashedpassword");

        // Act & Assert
        AuthenticationService spyAuthService = spy(authenticationService);
        CustomException exception = assertThrows(CustomException.class, () -> {
            spyAuthService.authenticate(request);
        });
        assertEquals(ErrorResponse.UNAUTHENTICATED, exception.getErrorResponse());
    }
}
