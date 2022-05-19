package exchange.kanga.configuration

import exchange.kanga.authentications.TokenProvider
import exchange.kanga.domain.backoffice.reqres.ServiceData
import exchange.kanga.domain.backoffice.reqres.ServiceLoginRequest
import exchange.kanga.utils.common.Service
import org.springframework.context.ApplicationContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object LoginConfiguration {
    fun getAuthToken(service: Service, login: ServiceLoginRequest, ctx: ApplicationContext): String? {
        val passwordEncoder = ctx.getBean("passwordEncoder", BCryptPasswordEncoder::class.java)
        return if (passwordEncoder.matches(login.password, service.password)) TokenProvider.generateToken(ServiceData(service.serviceName)) else null
    }
}

