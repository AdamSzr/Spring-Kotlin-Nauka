package exchange.kanga.configuration

import exchange.kanga.authentications.TokenProvider
import exchange.kanga.domain.backoffice.reqres.UserLoginRequest
import exchange.kanga.utils.common.User
import org.springframework.context.ApplicationContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object LoginConfiguration {

    fun getAuthToken(user: User, login: UserLoginRequest, ctx: ApplicationContext): String? {
        val passwordEncoder = ctx.getBean("passwordEncoder", BCryptPasswordEncoder::class.java)
        return if (passwordEncoder.matches(login.password, user.password)) TokenProvider.generateToken(user) else null
    }
}

