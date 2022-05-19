package exchange.kanga.utils




import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.regex.Pattern.compile

fun String?.tryParseToInstant() = InstantParser.parse(this)
fun String.toBigDecimalReverse(scale: Int = 8) = BigDecimal.ONE.divide(BigDecimal(this), scale, RoundingMode.HALF_UP)
fun String.standardizedEmail() = this.lowercase(Locale.getDefault()).trim()

infix fun String.toWallet(toWallet: String): Pair<String, String> =
    Pair(this, toWallet)

infix fun String.toBlockChain(blockChainAddress: String): Pair<String, String> =
    Pair(this, blockChainAddress)

fun String.isValidBlockchainAddress() =
    this.matches(Regex("(0x)?[0-9a-fA-F]{40}"))

private val emailRegex = compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

fun String.isValidEmail(): Boolean = emailRegex.matcher(this).matches()
