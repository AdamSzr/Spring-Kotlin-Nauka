package exchange.kanga.domain.cache

import exchange.kanga.CACHE_SVC_ROOT_DIR
import exchange.kanga.domain.cache.exception.FileNotFoundException
import exchange.kanga.providers.Drive
import exchange.kanga.utils.common.*
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import kotlin.io.path.Path
import kotlin.io.path.notExists

@RestController
@RequestMapping("/data")
class DataController(
    private val drive: Drive
) {



    @GetMapping(
        value = ["/{service}/{filename}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getFile(
        authentication: Authentication?,
        @PathVariable filename: String,
        @PathVariable service: String
    ): Any {
        var absolutePathString =  CACHE_SVC_ROOT_DIR.plus("/${service}/${filename}")
        val absolutePath = Path(absolutePathString)
        val fullPath = absolutePath.toFile()
        val parentPath = absolutePath.parent.toFile()

        if (!parentPath.exists())
            return PathNotExist()

        if (fullPath.isDirectory)
            return RequestedDirectory()

        return try {
            drive.getFile(fullPath.absolutePath) ?: FileNotFound()
        } catch (e: FileNotFoundException) {
            FileNotFound()
        } catch (e: Exception) {
            UnknownFailResponse(e.message ?: "Unknown error.")
        }
    }

    @PutMapping(
        value = ["/{service}/{filename}"],
    )
    fun updateResource(
        authentication: Authentication?,
        @PathVariable service: String,
        @PathVariable filename: String,
        @RequestBody update: String
    ): Any {
        if (authentication == null) return NullAuthentication()
        if (!authentication.isAuthenticated) return NoAuthorization()
        if (service != authentication.name) return NoPrivilegesToUpdate()

        val absolutePathString = CACHE_SVC_ROOT_DIR.plus("/${service}/${filename}")
        val filePath = Path(absolutePathString)
        val parentPath = filePath.parent
        if (parentPath.notExists()) return PathNotExist()

        drive.createOrOverrideTextFile(absolutePathString, update)
        return JsonUpdateResponse()
    }
}


