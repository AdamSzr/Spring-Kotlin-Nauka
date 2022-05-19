package exchange.kanga.domain.bon.service

import exchange.kanga.domain.bon.cache.VoucherCurrenciesCache
import org.springframework.stereotype.Service
import java.math.BigDecimal


@Service
class MinimumDepositValidator(
    private val voucherCurrenciesCache: VoucherCurrenciesCache
) {

    fun validate(currency: String, network: String, amount: BigDecimal): Boolean {
        val required: BigDecimal = getRequired(currency, network) ?: return false
        return required <= amount
    }

    fun getRequiredIfNotHaveMinimumAmount(currency: String, network: String, amount: BigDecimal): BigDecimal? {
        val required: BigDecimal = getRequired(currency, network) ?: return null
        return if (required > amount) required else null
    }

    private fun getRequired(currency: String, network: String): BigDecimal? =
        voucherCurrenciesCache.getMinimalDeposit(currency, network)
}