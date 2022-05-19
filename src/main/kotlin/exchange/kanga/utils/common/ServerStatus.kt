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

    private fun getStatusOkForLearn(learnType: LearnType): Boolean =
        serverStatus.statusLearn[learnType] == true

    fun setStatusOkForLearn(learnType: LearnType) {
        if (getStatusOkForLearn(learnType)) return

        serverStatus.statusLearn[learnType] = true

        info("Server Status: OFF -> Learn: $learnType: OK - - - > Learns: ${serverStatus.statusLearn.entries}")

        if (checkAllLearnOk()) setStatus(Status.ON)
    }


    private fun checkAllLearnOk(): Boolean = serverStatus.statusLearn.values.all { it }

    enum class Status { ON, OFF }
    enum class LearnType { UNKNOWN }
}

data class ServerStatus(
    var status: ServerStatusService.Status,
    @JsonInclude(JsonInclude.Include.NON_NULL) var startupTime: Long? = null,
    val timestamp: Instant = Instant.now(),
    val statusLearn: MutableMap<ServerStatusService.LearnType, Boolean>,
    @JsonInclude(JsonInclude.Include.NON_NULL) var message: String? = null,
) {
    // Server OFF
    internal constructor(message: String? = null) : this(
        status = ServerStatusService.Status.OFF,
        message = message ?: "restart",
        statusLearn = ServerStatusService.LearnType.values().toList().associateWith { false }.toMutableMap()
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