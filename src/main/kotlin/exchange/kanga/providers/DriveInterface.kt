package exchange.kanga.providers

import exchange.kanga.structures.DriveObject
import java.nio.file.Path

interface DriveInterface {
    fun directoryExist(path: String): Boolean
    fun isDirectory(path: String): Boolean
    fun isFile(path: String): Boolean
    fun listInnerItems(path: Path): List<DriveObject>
    fun createDirectory(path: String): Path
    fun getFile(filename: String): ByteArray?
    fun createOrOverrideTextFile(path: String, newDocument: String)
}