package exchange.kanga.utils

import exchange.kanga.structures.DriveObject
import exchange.kanga.structures.DriveObjectType
import java.io.File
import java.nio.file.Path

object DirectoryScanner {
    private fun getFilesFromDir(dir: Path): Array<File> {
        return File(dir.toString()).listFiles() ?: emptyArray()
    }

    fun scan(path: Path): List<DriveObject> {
        val filePaths = getFilesFromDir(path)
        return filePaths.map { f ->
            val type = if (f.isDirectory) DriveObjectType.DIR else DriveObjectType.FILE
            DriveObject(f, type)
        }
    }
}
