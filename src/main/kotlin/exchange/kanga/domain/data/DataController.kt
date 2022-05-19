package exchange.kanga.domain.data

import exchange.kanga.domain.bon.response.FileNotExist
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File


@RestController
@RequestMapping("/data")
class DataController {

    private val path = "src/main/resources/data"

    @GetMapping("/{filename}")
    fun getFile(@PathVariable filename: String): Any {
        return try {
            File("$path/$filename").readBytes()
        } catch (e: Exception) {
            return FileNotExist()
        }
    }
}
