package exchange.kanga.domain.bon.service.kanga

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KangaAuthService {

    @Value("#{\${appCredentials}}")
    private lateinit var appCredentials: Map<String, String>

    val appId: String
        get() = getCredentials("appId")

    val secret: String
        get() = getCredentials("secret")

    val apiKey: String
        get() = getCredentials("apiKey")

    private fun getCredentials(key: String) =
        appCredentials[key] ?: error("Kanga '$key' didn't set")
}
