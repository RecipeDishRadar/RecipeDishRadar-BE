package com.ducnt.recipedishradar.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorResponse {
    /** ERROR RESPONSE FOR ACCOUNT **/
    FULLNAME_INVALID("FullName must be at least {min} character ", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("Password must be at least {min} character ", HttpStatus.BAD_REQUEST),
    DOB_INVALID("Date of birth muse be at least {min}", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("Email must be a well-formed email address",  HttpStatus.BAD_REQUEST),
    PHONE_INVALID("Phone number is not in Vietnamese syntax", HttpStatus.BAD_REQUEST),
    ACCOUNT_IS_NOT_VERIFIED("Account did not verified otp please try again", HttpStatus.UNAUTHORIZED),
    ACCOUNT_IS_INACTIVE("Account has been deactivated please contact admin", HttpStatus.UNAUTHORIZED),
    ACCOUNT_HAS_ONE_AVATAR("Account just need one image for avatar", HttpStatus.BAD_REQUEST),
    ACCOUNT_CAN_NOT_CHANGE_EMAIL("Email must be not edited", HttpStatus.BAD_REQUEST),

    /** ERROR RESPONSE FOR FILE **/
    FILE_OVER_SIZE("Max file size is {min} MB", HttpStatus.BAD_REQUEST),
    IMAGE_FILE_INVALID("Only jpg, png, gif, bmp files are allowed or not empty file", HttpStatus.BAD_REQUEST),
    IO_FILE_INVALID("File is not an I/O file", HttpStatus.BAD_REQUEST),

    /** COMMON ERROR RESPONSE **/
    BAD_REQUEST("Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    NOT_FOUND("Data not found", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER("Unspecified error at server", HttpStatus.INTERNAL_SERVER_ERROR),

    TIME_INVALID("Time is not correct", HttpStatus.BAD_REQUEST);

    private String message;
    private HttpStatusCode httpStatusCode;
}
