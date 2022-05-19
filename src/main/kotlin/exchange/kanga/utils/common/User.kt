package exchange.kanga.utils.common

import exchange.kanga.domain.backoffice.reqres.UserSignUpRequest
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class User(
    @Id val nickname: String,
    var password: String,
    var role: UserRole = UserRole.USER,
    val details: Map<String, String> = mapOf(),
    @DBRef val permissions: List<Permission> = listOf(),
    val created: Instant = Instant.now(),
    val activated: Boolean = true,
) {
    fun changeRole(newRole: UserRole) =
        this.apply { role = newRole }
}

enum class UserRole {
    ADMIN, USER, MODERATOR, PARTNER
}

@Document
data class Permission(
    @Id val name: String,
    val description: String?
)

object UserUtils {

    fun singUp(signup: UserSignUpRequest, encryptedPassword: String) =
        User(
            nickname = signup.nickname,
            password = encryptedPassword,
            role = UserRole.ADMIN,
            details = mapOf("email" to signup.email)
        )

    fun changeRole(user: User, newRole: UserRole) =
        user.apply { role = newRole }
}