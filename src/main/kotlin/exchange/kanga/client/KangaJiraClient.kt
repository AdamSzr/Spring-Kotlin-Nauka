package exchange.kanga.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import exchange.kanga.utils.common.Logger
import exchange.kanga.utils.common.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KangaJiraClient(
    private val unirestClient: UnirestClient,
) {

    private companion object : Logger

    @Value("\${api.service.jira.kanga}")
    private lateinit var url: String

    fun sendRequest(message: String) {
        val response = getClient()
            .post(url)
            .body(ServiceToJiraRequest(message))

        val responseNode = response.asJson()

        val responseObject = jacksonObjectMapper().readValue<ServiceToJiraResponse>(responseNode.body.toString())

        info("Send Jira Request: $responseObject - Message: $message")
    }

    private fun getClient() =
        unirestClient.getClient()

    private data class ServiceToJiraRequest(
        val message: String,
        val email: String = "service+bon2@kanga.exchange",
    )

    private data class ServiceToJiraResponse(val issueId: String) : Response()
}