package exchange.kanga.domain.learn.library.event

import exchange.kanga.domain.learn.library.structures.BookModel
import org.springframework.context.ApplicationEvent


data class BookAddEvent (val book :BookModel) : ApplicationEvent(book) {
}