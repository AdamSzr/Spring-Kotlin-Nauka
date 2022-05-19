package exchange.kanga.client

import exchange.kanga.utils.common.Logger
import kong.unirest.ObjectMapper
import kong.unirest.Unirest
import kong.unirest.UnirestInstance
import kong.unirest.jackson.JacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class UnirestClient {

    private companion object : Logger

    private lateinit var client: UnirestInstance

    @PostConstruct
    private fun config() {
        client = Unirest.spawnInstance()

        client.config()
            .setObjectMapper((JacksonObjectMapper() as ObjectMapper))
            .setDefaultHeader("Accept", "application/json")
            .setDefaultHeader("Content-Type", "application/json")
    }

    @Bean
    fun getClient() = client
}


