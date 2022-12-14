package sizhe.chen.advice;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
//import org.springframework.web.servlet.NoHandlerFoundException;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ResopnseAdvice
 *
 * @Author chensizhe
 * @Date 2022/11/25 8:32 PM
 */

//@ControllerAdvice
public class ResponseAdvice
//        extends ResponseEntityExceptionHandler
{
    @RequiredArgsConstructor
    @Data
    public static class Error {
        private final List<InvalidField> invalidFields;
        private final List<String> errors;
    }

    @RequiredArgsConstructor
    @Data
    public static class InvalidField {
        private final String name;
        private final String message;
    }



//        @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers,
//                                                                  HttpStatus status,
//                                                                  WebRequest request) {
//        List<InvalidField> invalidFields = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> new InvalidField(error.getField(), error.getDefaultMessage()))
//                .collect(Collectors.toList());
//        List<String> errors = ex.getBindingResult().getGlobalErrors().stream()
//                .map(ObjectError::getDefaultMessage)
//                .collect(Collectors.toList());
//        Error error = new Error(invalidFields, errors);
//        return handleExceptionInternal(ex, error, headers, status, request);
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
//                                                                   HttpHeaders headers,
//                                                                   HttpStatus status,
//                                                                   WebRequest request) {
//        Error error = new Error(Collections.emptyList(), Arrays.asList(
//                String.format("No handler for %s %s", ex.getHttpMethod(), ex.getRequestURL())
//        ));
//        return handleExceptionInternal(ex, error, headers, status, request);
//    }
}
