package exchange.kanga.providers

import exchange.kanga.structures.DriveObject
import exchange.kanga.utils.DirectoryScanner
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

@Service
class Drive : DriveInterface {
    override fun directoryExist(path: String): Boolean {
        return isDirectory(path)
    }

    override fun isDirectory(path: String): Boolean {
        return File(path).isDirectory
    }

    override fun isFile(path: String): Boolean {
        return File(path).isFile
    }

    override fun listInnerItems(path: Path): List<DriveObject> {
        return DirectoryScanner.scan(path)
    }

    override fun createDirectory(path: String): Path {
        return Files.createDirectory(Path(path))
    }

    /**
     * Retrieve file from specified *path*.
     * @param path Absolute file path, can be without file extension.
     */
    override fun getFile(path: String): ByteArray? {
        return if (isFile(path)) File(path).readBytes()
        else null
    }

    override fun createOrOverrideTextFile(path: String, newDocument: String) {
        val bufferedWriter = BufferedWriter(FileWriter(path))
        bufferedWriter.write(newDocument)
        bufferedWriter.close()
    }
}