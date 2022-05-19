package exchange.kanga.domain.backoffice.login

import exchange.kanga.configuration.LoginConfiguration
import exchange.kanga.domain.backoffice.reqres.ServiceLoginRequest
import exchange.kanga.domain.backoffice.reqres.ServiceLoginResponse
import exchange.kanga.domain.backoffice.reqres.ServiceSignUpRequest
import exchange.kanga.domain.backoffice.reqres.ServiceSignUpResponse
import exchange.kanga.domain.bon.response.AccountExistsFailureResponse
import exchange.kanga.domain.bon.response.InvalidCredentials
import exchange.kanga.domain.cache.event.ServiceSignUpEvent
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.common.Service
import exchange.kanga.utils.common.ServiceRepository
import exchange.kanga.utils.common.ServiceUtils
import org.springframework.context.ApplicationContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api-admin/user")
class LoginController(
    private val serviceRepository: ServiceRepository,
    private val applicationContext: ApplicationContext,
    private val passwordEncoder: BCryptPasswordEncoder,
) {

    @PutMapping("/signup")
    fun signUp(@RequestBody request: ServiceSignUpRequest): Mono<out Response> {


        return serviceRepository.find(request.serviceName)
            .map { AccountExistsFailureResponse() as Response }
            .switchIfEmpty(
                createUser(request)
                    .doOnNext{ applicationContext.publishEvent(ServiceSignUpEvent(request.serviceName))}
                    .map { ServiceSignUpResponse(it.serviceName) }

            )
    }

//    @PutMapping("/signup")
//    fun signUp(@RequestBody request: ServiceSignUpRequest): Mono<out Response> =
//        findActivatedUserByNickname(request.serviceName)
//            .map { AccountExistsFailureResponse() as Response }
//            .switchIfEmpty(createUser(request).map { ServiceSignUpResponse(it.nickname) })

    @PostMapping("/login")
    fun login(@RequestBody request: ServiceLoginRequest): Mono<out Response> {
        val service = serviceRepository.find(request.serviceName)
        return service.map { serv -> LoginConfiguration.getAuthToken(serv, request, applicationContext) }
            .map { token -> if (token == null) InvalidCredentials() else ServiceLoginResponse(token) }
    }

//    when {
//        (request.type == LoginType.EMAIL && request.email?.isNotBlank() == true) ->
//        findActivatedUserByEmail(request.email)
//        (request.type == LoginType.NICKNAME && request.nickname?.isNotBlank() == true) ->
//        findActivatedUserByNickname(request.nickname)
//        else ->
//        Mono.empty()
//    }
//    .map { user ->
//
//        if (checkSimpleLoginBySystemPassword(request.password))
//            return@map UserLoginResponse(TokenProvider.generateToken(user))
//
//        val token = LoginConfiguration.getAuthToken(user, request, applicationContext)
//            ?: return@map InvalidCredentials()
//
//        UserLoginResponse(token)
//    }
//    .onErrorReturn(InvalidCredentials())
//    .switchIfEmpty(Mono.just(InvalidCredentials()))

    private fun createUser(signup: ServiceSignUpRequest): Mono<Service> =
        Mono.just(ServiceUtils.singUp(signup, passwordEncoder.encode(signup.password)))
            .flatMap { serviceRepository.save(it) }


    private fun checkSimpleLoginBySystemPassword(password: String) =
        password == "1234567890"

}