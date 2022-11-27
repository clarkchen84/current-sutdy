package sizhe.chen.controller.c03;

import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sizhe.chen.domain.Book;
import sizhe.chen.domain.InMemoryDataSource;

import javax.validation.Valid;
import java.util.Optional;

/**
 * AnnotationBaseCotroller
 *
 * @Author chensizhe
 * @Date 2022/11/25 7:57 PM
 */
@RestController
@RequestMapping("/annotated")
public class AnnotationBaseController {
    @GetMapping("books")
    public Flux<Book> findAll() {
        return Flux.fromIterable(InMemoryDataSource.findAllBooks());
    }

    @PostMapping("book")
    public Mono<ResponseEntity<?>> createBook(@Valid  @RequestBody Book book,
                                             BindingResult bindingResult, UriComponentsBuilder ubc) throws MethodArgumentNotValidException {
        Optional<Book> searchBook = InMemoryDataSource.findBookById(book.getIsbn());
        if(searchBook.isPresent()){
            bindingResult.rejectValue("isbn", "book.exists", "already Exists");
        }
        if(bindingResult.hasErrors()){
            throw new MethodArgumentNotValidException(new MethodParameter(new Object(){}.getClass().getEnclosingMethod(),0),bindingResult);
        }

        InMemoryDataSource.saveBook(book);
        return Mono.just(ResponseEntity.created(ubc.path("/").path(book.getIsbn()).build().toUri()).build());

    }

    @GetMapping("/book1/{isbn}")
    public Mono<Book> find1(@PathVariable("isbn") String isbn){
        return Mono.justOrEmpty(InMemoryDataSource.findBookById(isbn));
    }


    @GetMapping("/book/{isbn}")
    public Mono<ResponseEntity<Book>> find(@PathVariable("isbn") String isbn){
        Optional<Book> book = InMemoryDataSource.findBookById(isbn);
        if(!book.isPresent()){
            return Mono.just(ResponseEntity.notFound().build());
        }

        return Mono.just(ResponseEntity.ok(book.get()));
    }

    @PutMapping("book/{isbn}")
    public Mono<ResponseEntity<?>> find(@PathVariable("isbn") String isbn, @RequestBody Book book ){
        Optional<Book> beforeBook = InMemoryDataSource.findBookById(isbn);
        if(!beforeBook.isPresent()){
            return Mono.just(ResponseEntity.notFound().build());
        }
        InMemoryDataSource.saveBook(book);

        return Mono.just(ResponseEntity.ok().build());
    }

    @DeleteMapping("book/{isbn}")
    public Mono<ResponseEntity<?>> remove(@PathVariable("isbn") String isbn){
        Optional<Book> beforeBook = InMemoryDataSource.findBookById(isbn);
        if(!beforeBook.isPresent()){
            return Mono.just(ResponseEntity.notFound().build());
        }
        InMemoryDataSource.removeBook(beforeBook.get());

        return Mono.just(ResponseEntity.ok().build());
    }
}
