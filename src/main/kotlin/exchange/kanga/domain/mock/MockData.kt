package exchange.kanga.domain.mock

import exchange.kanga.domain.bon.model.*
import exchange.kanga.domain.bon.utils.HashMethod
import exchange.kanga.domain.bon.utils.StringGeneratorUtils
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class MockData {

    fun generatePartners(int: Int) =
        (1..int).map { generatePartner() }.toList()

    fun generateVouchers(int: Int) =
        (1..int).map { generateVoucher() }.toList()


    fun generatePartner(): Partner {
        val x = Math.random().times(10).toInt()
        val y = Math.random().times(10).toInt()

        return Partner(
            StringGeneratorUtils.getRandomString(8),
            StringGeneratorUtils.getRandomString(24),
            BillingMode.OFF_CHAIN,
            "www.partner.url/sign",
            "www.partner.url/update",
            voucherExpireTime = if (y<3) Duration.ofDays(30) else null,
            if (x<5) HashMethod.SHA256 else HashMethod.MD5,
            "someWalletName"
        )
    }

    fun generateVoucher(): Voucher {
        val x = Math.random().times(10).toInt()
        val y = Math.random().times(10).toInt()
        val z = Math.random().times(10).toInt()

        val randomDigit1 = StringGeneratorUtils.getRandomDigitsString (1).toLong()

        val state = State.values().random()
        val history = mutableListOf(
            VoucherHistory(time = Instant.now().minus(randomDigit1, ChronoUnit.DAYS)),
            VoucherHistory(state = state)
        )

        val currencies = listOf("oPLN","oEUR","BTC","ETH")

        return Voucher(
            id = StringGeneratorUtils.getRandomString(8),
            pin = StringGeneratorUtils.getRandomDigitsString (4),
            partnerId = StringGeneratorUtils.getRandomString(8),
            type = VoucherType.values().random(),
            currency =  currencies.random(),
            network = "ETHER",
            address = if (y<3) "0x${StringGeneratorUtils.getRandomString(24)}" else "",
            operator = Operator.ANY,
            expire = if (z<2) Instant.now().plus(StringGeneratorUtils.getRandomDigitsString (1).toLong(), ChronoUnit.DAYS) else null,
            state = state,
            history = history,
            email = "",
            kyc = x<5,
            control = StringGeneratorUtils.getRandomString(4),
        )
    }
}