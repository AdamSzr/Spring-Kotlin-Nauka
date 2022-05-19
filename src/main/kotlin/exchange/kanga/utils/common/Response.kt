package exchange.kanga.utils.common


open class Response(val result: String = "ok")

sealed class FailureResponse(val code: Int, val error: String) : Response("fail")

class NoAuthorization : FailureResponse(9997, "no-authorization")
class NullAuthentication : FailureResponse(9996, "auth-null")

class PathNotExist : FailureResponse(1, "Requested path does not exist.")

class RequestedDirectory : FailureResponse(2, "Requested path points on directory.")

class FileNotFound : FailureResponse(3, "Requested file does not exist.")
class UnknownFailResponse(message: String) : FailureResponse(4, message)

class NoPrivilegesToUpdate : FailureResponse(5, "Can not edit content outside of your directory.")
class JsonUpdateResponse(): Response()