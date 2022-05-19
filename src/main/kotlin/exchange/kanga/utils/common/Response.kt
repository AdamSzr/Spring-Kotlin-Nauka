package exchange.kanga.utils.common

open class Response(val result: String = "ok"): Throwable()

sealed class FailureResponse(val code: Int, val error: String): Response("fail") {
    override val message: String? = error
}

class NoAuthorization: FailureResponse(9997, "no-authorization")
class NullAuthentication: FailureResponse(9996, "auth-null")