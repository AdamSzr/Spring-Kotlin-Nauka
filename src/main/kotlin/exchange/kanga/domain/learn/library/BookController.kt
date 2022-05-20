package exchange.kanga.domain.learn.library

import exchange.kanga.domain.learn.library.service.LibrarySvc
import exchange.kanga.domain.learn.library.structures.BookModel
import exchange.kanga.utils.UtilTools
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono


@RestController
@RequestMapping("/library")
class BookController(
    val librarySvc: LibrarySvc
) {

    @GetMapping(value = ["/test"], produces = ["text/event-stream"])
    fun handleTest(): Flux<BookModel> {
        return (0..10).toFlux()
            .map { BookModel.random() }
            .flatMap { m -> librarySvc.saveBook(m) }
            .doOnNext { it -> println(it) }
    }

    @GetMapping(value = ["/count/{count}"], produces = ["text/event-stream"])
    fun handleToUpper(@PathVariable count: Long): Flux<BookModel> {
        return librarySvc.getBooks(count)
    }

    @GetMapping(value = ["/example"])
    fun handleExampleBook(): BookModel {
        return BookModel("1234", "Example book", "Programmer")
    }

    @GetMapping
    fun handleAllBooks(): Flux<BookModel> {
        return librarySvc.getAllBooksByAuthor()
    }

    @GetMapping(value = ["/compute"])
    fun compute(): Any {
        var z = 0;
        var jump = 10;

        return librarySvc.getAllBooksByAuthor()
            .map { b ->
                b.author
                    .plus(b.barcode)
                    .plus(UtilTools.randomString(5))
                    .toUpperCase()
                    .replace("ADAM", "")
            }
            .reduce { z, b -> z.plus(b) }
            .map { s -> s.length }
    }


    @GetMapping("/books/available")
    fun handleOnlyAvailableBooks(): Flux<BookModel> {
        return librarySvc.getAllBooksByAuthor().filter { b -> !b.booked }
    }

    @PostMapping
    fun saveBook(@RequestBody bookModel: BookModel): Mono<BookModel> {
        return librarySvc.saveBook(bookModel)
    }


}