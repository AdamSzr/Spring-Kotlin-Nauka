package exchange.kanga.utils.common

import exchange.kanga.domain.backoffice.reqres.ServiceSignUpRequest
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Service(
    @Id val serviceName: String,
    var password: String,
    val created: Instant = Instant.now(),
    val activated: Boolean = true,
) {
}

object ServiceUtils {
    fun singUp(signup: ServiceSignUpRequest, encryptedPassword: String) =
        Service(
            serviceName = signup.serviceName,
            password = encryptedPassword,
        )
}