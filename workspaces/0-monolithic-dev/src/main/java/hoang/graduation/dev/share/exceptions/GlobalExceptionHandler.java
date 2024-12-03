package hoang.graduation.dev.share.exceptions;

import hoang.graduation.dev.share.model.response.WrapResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<WrapResponse<?>> handleGeneralException(Exception exception) {
        return ResponseEntity.internalServerError().body(
                WrapResponse.builder()
                        .isSuccess(false)
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WrapResponse.builder()
                        .isSuccess(false)
                        .status(HttpStatus.NOT_FOUND)
                        .message(exception.getMessage())
                        .build());
    }

}
