package exchange.kanga.domain.backoffice.wallet

import exchange.kanga.domain.bon.service.kanga.KangaWalletService
import exchange.kanga.utils.common.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class WalletFulfillScheduler(
    private val kangaWalletService: KangaWalletService,
) {

    private companion object : Logger

    private val requireMinimum = mapOf(
        "oPLN" to BigDecimal("100000"),
        "oEUR" to BigDecimal("10000"),
    )

    @Scheduled(cron = "0 0 */1 * * ?")
    private fun check() {
        val wallet = kangaWalletService.getBalances(null)

        requireMinimum
            .filter { (wallet[it.key]?.value ?: BigDecimal.ZERO) < it.value   }
            .forEach { sendJiraRequest(it, requireMinimum[it.key]) }
    }

    private fun sendJiraRequest(entry: Map.Entry<String, BigDecimal>, require: BigDecimal?) {
        error("OTC Wallet: (coin: ${entry.key} - minimum: $require - jest: ${entry.value} ", false)
    }
}