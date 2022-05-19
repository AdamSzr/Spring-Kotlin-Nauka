package exchange.kanga.domain.backoffice.reqres

import exchange.kanga.utils.common.Response

data class UserLoginRequest(
    val type: LoginType,
    val email: String? = null,
    val nickname: String? = null,
    val password: String,
)

enum class LoginType { EMAIL, NICKNAME }

data class UserLoginResponse(val token: String) : Response()

data class UserSignUpRequest(
    val nickname: String,
    val email: String,
    val password: String,
)

data class UserSignupResponse(val nickname: String) : Response()