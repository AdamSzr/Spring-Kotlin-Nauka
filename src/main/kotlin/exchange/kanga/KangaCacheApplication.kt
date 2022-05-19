package exchange.kanga

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KangaCacheApplication

fun main(args: Array<String>) {
    runApplication<KangaCacheApplication>(*args)
}
