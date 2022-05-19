package exchange.kanga.utils.common

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/ping")
class Ping {

    private companion object : Logger

    @GetMapping
    fun ping(request: ServerHttpRequest): Response {
        logRequest(
            "GET:/api/ping",
            "(remote: ${request.remoteAddress} local: ${request.localAddress})",
            request.headers.toSingleValueMap(),
        )
        return Response()
    }
}
