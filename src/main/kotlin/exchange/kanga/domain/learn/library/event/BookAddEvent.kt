package exchange.kanga.domain.learn.library.event

import exchange.kanga.domain.learn.library.structures.BookModel


data class BookAddEvent (val book :BookModel) {
}