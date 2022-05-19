package exchange.kanga.utils.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.InetAddress
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/api-admin/env")
class Env(private val environment: Environment) {

    private companion object : Logger


    private lateinit var env: String

    @PostConstruct
    private fun onInit() {
        env = """
        Environment: 
            Env SpringProfile: ${environment.getProperty("spring_profiles_active")}
            Env Port (local server port): : ${environment.getProperty("local.server.port")}
            Env Port (server port): : ${environment.getProperty("server.port")}
            CanonicalHostName: ${InetAddress.getLocalHost().canonicalHostName}
            HostAddress: ${InetAddress.getLocalHost().hostAddress} 
            HostName: ${InetAddress.getLocalHost().hostName}
            LoopbackAddress CanonicalHostName: ${InetAddress.getLoopbackAddress().canonicalHostName}
            LoopbackAddress HostAddress: ${InetAddress.getLoopbackAddress().hostAddress}
            LoopbackAddress HostName: ${InetAddress.getLoopbackAddress().hostName}
            """

        info(env)
    }

    /*
    Port: : 8081
    CanonicalHostName: 7908b80f7b40
    HostAddress: 192.168.192.2
    HostName: 7908b80f7b40
    LoopbackAddress CanonicalHostName: localhost
    LoopbackAddress HostAddress: 127.0.0.1
    LoopbackAddress HostName: localhost
     */
    @GetMapping("/server")
    fun getEnv(authentication: Authentication?): Any =
        if (authentication != null)
            env
        else
            NoAuthorization()

    /*
    remote
    /85.237.183.205:56988
    /85.237.183.205
    85.237.183.205
    56988
    85.237.183.205
    local
    /172.23.0.2:8082
    /172.23.0.2
    1616f3d2be99
    8082
    1616f3d2be99
    body:
    FluxMap
    headers
    [Content-Type:"application/json", User-Agent:"PostmanRuntime/7.29.0", Accept:"* / *", Cache-Control:"no-cache", Postman-Token:"5717b38f-c7fc-417f-9f40-dda60e3ca2e1", Host:"dev.kanga.team:8882", Accept-Encoding:"gzip, deflate, br", Connection:"keep-alive", content-length:"151"]
     */
    @GetMapping("/client")
    fun getClientEnv(request: ServerHttpRequest): String {
        val response = """
        Environment Client Request
            remote:
                ${request.remoteAddress}
                address: ${request.remoteAddress?.address}
                hostName: ${request.remoteAddress?.hostName}
                port: ${request.remoteAddress?.port}
                hostString: ${request.remoteAddress?.hostString}
            
            local:
                ${request.localAddress}
                address: ${request.localAddress?.address}
                hostName: ${request.localAddress?.hostName}
                port: ${request.localAddress?.port}
                hostString: ${request.localAddress?.hostString}
        
            headers:
                ${request.headers}
        """

        info(response)

        return response.trimIndent()
    }
}
