package sizhe.chen.advice;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import sizhe.chen.controller.helper.ControllerHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GlobalErrorWebExceptionHandler
 *
 * @Author chensizhe
 * @Date 2022/11/26 1:18 PM
 */
@Slf4j
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                          ResourceProperties resourceProperties, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), request -> {
            var throwable = errorAttributes.getError(request);
            if(throwable instanceof ControllerHelper.ValidationException){
                return handleValidationException((ControllerHelper.ValidationException) throwable);
            }

            if(throwable instanceof  ResponseStatusException){
                return handleValidationException((ControllerHelper.ValidationException) throwable);
            }
            log.error("Ops, just caught an unknown exception, " +
                    "please have a look at the stack trace of more details", throwable);
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
    }

    private Mono<ServerResponse> handleValidationException(ControllerHelper.ValidationException exception){
        var errors =  exception.getErrors();
        var invalidFields = errors.getFieldErrors().stream().map(error ->
                new InvalidateField(error.getField(),error.getDefaultMessage())).collect(Collectors.toList());
        var theErrors = errors.getGlobalErrors().stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        return ServerResponse.badRequest().bodyValue(new Error(invalidFields,theErrors));
    }

    private Mono<ServerResponse> handleValidationException(ResponseStatusException exception){
        var error = new Error(null, Arrays.asList(  exception.getReason()));
        return ServerResponse.status(exception.getStatus()).bodyValue(error);
    }


    @RequiredArgsConstructor
    @Data
    private static class Error{
        private final List<InvalidateField> fields;
        private final List<String> errors;
    }

    @Data
    @RequiredArgsConstructor
    private static class InvalidateField{
        private final  String name;
        private  final String message;
    }
}
