package exchange.kanga.domain.learn.library.repository

import exchange.kanga.domain.learn.library.structures.BookModel
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface BookRepository: ReactiveMongoRepository<BookModel, String> {
    fun findByAuthor(author:String):Flux<BookModel>

    @Query("{}")
    fun all():Flux<BookModel>
}