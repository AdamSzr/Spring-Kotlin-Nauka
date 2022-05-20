package exchange.kanga.domain.learn.library.listener

import exchange.kanga.domain.learn.library.event.BookAddEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class BookAddListener {
    @EventListener
    fun onNewBookAdd(book: BookAddEvent) {
        println("1 ->"+book)
    }

}

@Component
class BookAddListener2: ApplicationListener<BookAddEvent> {

    override fun onApplicationEvent(event: BookAddEvent) {
        println("2 ->"+event)
    }
}