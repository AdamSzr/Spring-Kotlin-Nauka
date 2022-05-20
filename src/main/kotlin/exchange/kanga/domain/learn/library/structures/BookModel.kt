package exchange.kanga.domain.learn.library.structures

import exchange.kanga.utils.UtilTools
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Random


@Document
data class BookModel(val title: String, val author: String, val barcode: String, val booked: Boolean = false) {
    companion object {
        fun random(): BookModel {
            return BookModel(
                UtilTools.randomString(10),
                listOf("adam", "jan", "krzysztof", "kuba").random(),
                UtilTools.randomString(10),
                Random().nextBoolean(),
            )

        }
    }
}


