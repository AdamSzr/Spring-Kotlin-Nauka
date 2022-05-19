package exchange.kanga.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import exchange.kanga.domain.learn.response.SerializeUnknownProblem
import exchange.kanga.utils.common.Logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class WebClient {

    private companion object : Logger

    private val webClient by lazy { buildWebClient() }

    private fun buildWebClient(): WebClient = WebClient
        .builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    fun <T> fetchData(
        endpoint: String,
        classs: Class<T>,
        body: Any? = null,
        method: HttpMethod = HttpMethod.GET
    ): Mono<Any> {
        val request =
            webClient
                .method(method)
                .uri(endpoint)

        if (body != null)
            request
                .body(BodyInserters.fromValue(body))

        val response =
            request
                .retrieve()
                .bodyToMono(Any::class.java)
                .mapNotNull { jacksonObjectMapper().convertValue(it, classs) as Any }
                .doOnNext { info("Fetch Data: url: $method:$endpoint - response: $it - request: $body") }
                .doOnError { e -> error("Api problem: endpoint: $endpoint - data: $body - message: ${e.message}") }
                .onErrorResume {
                    error("Swap Serialize Unknown Problem: ${it.message}")
                    Mono.just(SerializeUnknownProblem(it.message))
                }

        return response
    }
}
