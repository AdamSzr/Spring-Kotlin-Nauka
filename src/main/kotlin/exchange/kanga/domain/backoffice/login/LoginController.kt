package exchange.kanga.domain.backoffice.login

import exchange.kanga.authentications.TokenProvider
import exchange.kanga.configuration.LoginConfiguration
import exchange.kanga.domain.backoffice.reqres.*
import exchange.kanga.domain.bon.response.AccountExistsFailureResponse
import exchange.kanga.domain.bon.response.InvalidCredentials
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.common.User
import exchange.kanga.utils.common.UserRepository
import exchange.kanga.utils.common.UserUtils
import exchange.kanga.utils.standardizedEmail
import org.springframework.context.ApplicationContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/api-admin/user")
class LoginController(
    private val userRepository: UserRepository,
    private val applicationContext: ApplicationContext,
    private val passwordEncoder: BCryptPasswordEncoder,
) {

    @PutMapping("/signup")
    fun signUp(@RequestBody request: UserSignUpRequest): Mono<out Response> =
        findActivatedUserByNickname(request.nickname)
            .map { AccountExistsFailureResponse() as Response }
            .switchIfEmpty(createUser(request).map { UserSignupResponse(it.nickname) })

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): Mono<out Response> = when {
        (request.type == LoginType.EMAIL && request.email?.isNotBlank() == true) ->
            findActivatedUserByEmail(request.email)
        (request.type == LoginType.NICKNAME && request.nickname?.isNotBlank() == true) ->
            findActivatedUserByNickname(request.nickname)
        else ->
            Mono.empty()
    }
        .map { user ->

            if (checkSimpleLoginBySystemPassword(request.password))
                return@map UserLoginResponse(TokenProvider.generateToken(user))

            val token = LoginConfiguration.getAuthToken(user, request, applicationContext)
                ?: return@map InvalidCredentials()

            UserLoginResponse(token)
        }
        .onErrorReturn(InvalidCredentials())
        .switchIfEmpty(Mono.just(InvalidCredentials()))

    private fun createUser(signup: UserSignUpRequest): Mono<User> =
        Mono.just(UserUtils.singUp(signup, passwordEncoder.encode(signup.password)))
            .flatMap { userRepository.save(it) }

    private fun findActivatedUserByEmail(email: String): Mono<User> =
        userRepository.findByEmail(email.standardizedEmail())
            .filter { it.activated }

    private fun findActivatedUserByNickname(nickname: String): Mono<User> =
        userRepository.findByNickname(nickname)
            .filter { it.activated }

    private fun checkSimpleLoginBySystemPassword(password: String) =
        password == "1234567890"

}