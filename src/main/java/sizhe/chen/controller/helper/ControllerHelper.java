package sizhe.chen.controller.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import javax.validation.valueextraction.ExtractedValue;
import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * ControllerHelper
 *
 * @Author chensizhe
 * @Date 2022/11/26 12:16 PM
 */
public class ControllerHelper {


    @RequiredArgsConstructor
    @Getter
    public static class ValidationException extends RuntimeException {
        private final  Errors errors;
    }

    @FunctionalInterface
    public interface ExtraValidator<T>{
        Mono<Tuple2<T,Errors>> validate(T t, Errors e);

    }

    public static <T> Mono<T> validate(Validator validator,Mono<T> mono){
        return validate(validator,null, mono);
    }

    public static <T> Mono<T> queryParamsToMono(ServerRequest request, ObjectMapper objectMapper, Class<T> cls,Validator validator){
        return queryParamsToMono(request,objectMapper,cls,validator,null);
    }
    public static <T> Mono<T> queryParamsToMono(ServerRequest request, ObjectMapper objectMapper, Class<T> cls,
                                                Validator validator,ExtraValidator<T> extraValidator){

    }

    public static <T> T convertValue(ObjectMapper objectMapper, MultiValueMap<String,String> map, Class<T> cls){
        if(map == null){
            return null;
        }
        var theMap = map.entrySet().stream().map(
                e-> {
                    String key = e.getKey();
                    List<String > list = e.getValue();
                    if(list != null && list.size() == 1){
                        return new AbstractMap.SimpleEntry<>(key,list.get(0));
                    }
                    return e;
                }
        ).collect(Collectors.toMap(e-> e.getKey(), e->e.getValue()));
        return objectMapper.convertValue(theMap,cls);
    }
    public static <T> Mono<T> validate(Validator validator,
                                       @Nullable   ExtraValidator<T> extraValidator, Mono<T> mono){
        return mono.flatMap(t -> {
            Errors errors = new BeanPropertyBindingResult(t, t.getClass().getName());
            validator.validate(t,errors);
            Mono<Tuple2<T,Errors>> aMono = Mono.empty();
            if(extraValidator != null){
                aMono = extraValidator.validate(t, errors);
            }

            return aMono.switchIfEmpty(Mono.just(Tuples.of(t,errors)));
        }).flatMap(tuple2 -> {
            var errors = tuple2.getT2();
            if(errors.hasErrors()){
                return Mono.error(new ValidationException(errors));
            }
            return Mono.just(tuple2.getT1());
        });
    }


    public static <T> Mono<T> requestBodyToMono(Validator validator, ServerRequest request,Class<T> cls){
        return validate(validator, request.bodyToMono(cls));
    }

    public  static <T> Mono<T> requestBodyToMono(Validator validator, ExtraValidator<T> extractedValue, ServerRequest request, Class<T> cls){
        return  validate(validator,extractedValue,request.bodyToMono(cls));
    }
}
