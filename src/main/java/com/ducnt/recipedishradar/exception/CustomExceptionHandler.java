package com.ducnt.recipedishradar.exception;

import com.ducnt.recipedishradar.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(ExistedException.class)
    public ResponseEntity<ApiResponse> handleExistedException(ExistedException e) {
        int badRequest = HttpStatus.BAD_REQUEST.value();
        var response = ResponseEntity.status(badRequest);
        return response.body(
                ApiResponse
                        .builder()
                        .code(badRequest)
                        .message(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse> handleExistedException(NotFoundException e) {
        int badRequest = HttpStatus.BAD_REQUEST.value();
        var response = ResponseEntity.status(badRequest);
        return response.body(
                ApiResponse
                        .builder()
                        .code(badRequest)
                        .message(e.getMessage())
                        .build()
        );
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(CustomException e) {
        ErrorResponse errorResponse = e.getErrorResponse();
        var response = ResponseEntity.status(errorResponse.getHttpStatusCode());
        return response.body(
                ApiResponse
                        .builder()
                        .code(errorResponse.getHttpStatusCode().value())
                        .message(errorResponse.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(CustomException e) {
        var response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.body(
                ApiResponse
                        .builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Internal server error!")
                        .build()
        );
    }
}
