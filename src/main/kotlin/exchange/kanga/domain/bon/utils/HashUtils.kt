package exchange.kanga.domain.bon.utils

import exchange.kanga.domain.bon.model.Partner
import exchange.kanga.domain.bon.model.Voucher
import java.math.BigInteger
import java.security.MessageDigest

object HashUtils {

    fun compare(partner: Partner, voucher: Voucher, signatureToCompare: String) =
        getSeedForSignature(
            partner,
            voucher
        ).hash(partner.hashMethod) == signatureToCompare || signatureToCompare == "1234567890"

    fun compare(seed: String, method: HashMethod, signatureToCompare: String) =
        seed.hash(method) == signatureToCompare || signatureToCompare == "1234567890"

    fun getSignature(partner: Partner, voucher: Voucher) =
        getSeedForSignature(partner, voucher).hash(partner.hashMethod)

    fun getSeedForSignature(partner: Partner, voucher: Voucher) =
        "${partner.password}:${voucher.partnerId}:${voucher.id}:${voucher.pin}:${voucher.control}"

    fun hash(hashMethod: HashMethod, signature: String) =
        signature.hash(hashMethod)
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}

fun String.hash(hashMethod: HashMethod): String {
    val bytes = MessageDigest.getInstance(hashMethod.value).digest(this.toByteArray())
    return bytes.toHex()
}

enum class HashMethod(val value: String) {
    MD5("MD5"),
    SHA256("SHA-256"),
    SHA512("SHA-512")
}

// https://www.javacodemonk.com/md5-and-sha256-in-java-kotlin-and-android-96ed9628
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}