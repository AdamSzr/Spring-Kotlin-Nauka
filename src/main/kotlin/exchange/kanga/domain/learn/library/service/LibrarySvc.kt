package exchange.kanga.domain.learn.library.service

import exchange.kanga.domain.learn.library.repository.BookRepository
import exchange.kanga.domain.learn.library.structures.BookModel
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class LibrarySvc(
   private val repository: BookRepository,
) {
    fun getAllBooksByAuthor(author: String = "adam"): Flux<BookModel> {
        return repository.findByAuthor(author);
    }

    fun getBooks(count:Long=50): Flux<BookModel> {
        return repository.all().take(count)
    }

    fun saveBook(book:BookModel): Mono<BookModel> {
        return repository.save(book);
    }

}