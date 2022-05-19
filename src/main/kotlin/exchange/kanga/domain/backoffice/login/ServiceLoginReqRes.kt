package exchange.kanga.domain.backoffice.reqres

import exchange.kanga.utils.common.Response

data class ServiceLoginRequest(
    val serviceName: String,
    val password:String
)

data class ServiceData(
    val serviceName: String
)


data class ServiceLoginResponse(val token: String) : Response()

data class ServiceSignUpRequest(
    val serviceName: String,
    val password:String
)

data class ServiceSignUpResponse(val serviceName: String) : Response()