package exchange.kanga.utils.common

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.Instant

@Service
final class ServerStatusService {

    private companion object : Logger

    private val serverStatus: ServerStatus = ServerStatus("restart")

    fun isOnline() = serverStatus.status == Status.ON

    internal fun getStatus() = serverStatus

    internal fun setStatus(newStatus: Status, message: String? = null) {
        serverStatus
            .apply {
                this.status = newStatus
                this.message = message
                if (newStatus == Status.ON)
                    this.startupTime = Duration.between(this.timestamp, Instant.now()).seconds
            }

        info("Server Status: ${serverStatus.status} - - - > $serverStatus")
    }

    private fun getStatusOkForCache(cacheType: CacheType): Boolean =
        serverStatus.statusCache[cacheType] == true

    fun setStatusOkForCache(cacheType: CacheType) {
        if (getStatusOkForCache(cacheType)) return

        serverStatus.statusCache[cacheType] = true

        info("Server Status: OFF -> Cache: $cacheType: OK - - - > Caches: ${serverStatus.statusCache.entries}")

        if (checkAllCacheOk()) setStatus(Status.ON)
    }


    private fun checkAllCacheOk(): Boolean = serverStatus.statusCache.values.all { it }

    enum class Status { ON, OFF }
    enum class CacheType { UNKNOWN }
}

data class ServerStatus(
    var status: ServerStatusService.Status,
    @JsonInclude(JsonInclude.Include.NON_NULL) var startupTime: Long? = null,
    val timestamp: Instant = Instant.now(),
    val statusCache: MutableMap<ServerStatusService.CacheType, Boolean>,
    @JsonInclude(JsonInclude.Include.NON_NULL) var message: String? = null,
) {
    // Server OFF
    internal constructor(message: String? = null) : this(
        status = ServerStatusService.Status.OFF,
        message = message ?: "restart",
        statusCache = ServerStatusService.CacheType.values().toList().associateWith { false }.toMutableMap()
    )
}

@RestController
@RequestMapping("/api/server-status")
class ServerStatusController(private val serverStatusService: ServerStatusService) {

    @GetMapping
    fun getServerStatus() =
        serverStatusService.getStatus()
}

@RestController
@RequestMapping("/api-admin/server-status")
class AdminServerStatusController(private val serverStatusService: ServerStatusService) {

    @PostMapping
    fun setStatus(@RequestBody body: ServerStatusRequest) =
        serverStatusService.setStatus(body.status, body.message)


    data class ServerStatusRequest(
        val status: ServerStatusService.Status,
        val message: String?
    )
}