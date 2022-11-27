package sizhe.chen.controller.c03;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import sizhe.chen.controller.helper.ControllerHelper;
import sizhe.chen.domain.Book;
import sizhe.chen.domain.InMemoryDataSource;

import java.util.Optional;


/**
 * RouteFunction
 *
 * @Author chensizhe
 * @Date 2022/11/25 9:17 PM
 */
@Configuration
@RequiredArgsConstructor
public class RouteBasedController {
    private static final String PATH_PREFIX = "/routed/";
    private final Validator validator;

    @Bean
    public RouterFunction<ServerResponse> routers() {
        return RouterFunctions.route()
                .POST(PATH_PREFIX + "book", this::create)
                .GET(PATH_PREFIX+"book/{isbn}",this::findBookById)
                .GET(PATH_PREFIX+"books",this::findAll)
                .GET(PATH_PREFIX + "books", this::findBypage)
                .build();
    }

    private Mono<ServerResponse> findBypage(ServerRequest serverRequest) {
    }

    private Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        var books = InMemoryDataSource.findAllBooks();
        return ServerResponse.ok().bodyValue(books);
    }

    private Mono<ServerResponse> create(ServerRequest request) {
        return  ControllerHelper.requestBodyToMono(validator,(book, error)->{
                    Optional<Book> bookOptional = InMemoryDataSource.findBookById(book.getIsbn());
                    if(bookOptional.isPresent()){
                       error.rejectValue("isbn","already.exist","Already exists");
                    }
                    return Mono.just(Tuples.of(book, error));
                },request,Book.class).map(InMemoryDataSource::saveBook)
                //.then(ServerResponse.ok().build());
                .flatMap(book ->
                        ServerResponse.created(UriComponentsBuilder.fromPath(PATH_PREFIX + "book").path(book.getIsbn()).build().toUri())
                        .build());
    }

    private Mono<ServerResponse> findBookById(ServerRequest request) {
        var isbn = request.pathVariable("isbn");

        return InMemoryDataSource.findBookMonoById(isbn).flatMap(book -> ServerResponse.ok().bodyValue(book))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
