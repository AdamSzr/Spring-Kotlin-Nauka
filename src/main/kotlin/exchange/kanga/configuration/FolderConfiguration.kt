package exchange.kanga.configuration

import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PostConstruct

@Configuration
class FolderConfiguration {

    @PostConstruct
    fun createFolderIfNotExist() {
        Files.createDirectories(Paths.get("src/main/resources/data/"));
    }
}