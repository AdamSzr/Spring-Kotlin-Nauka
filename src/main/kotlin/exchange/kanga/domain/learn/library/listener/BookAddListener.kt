package exchange.kanga.domain.learn.library.listener

import exchange.kanga.domain.learn.library.event.BookAddEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class BookAddListener {

    @EventListener
    fun onNewBookAdd(book:BookAddEvent){
        println(book)
    }
}