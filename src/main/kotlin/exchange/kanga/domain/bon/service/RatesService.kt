package exchange.kanga.domain.bon.service

import exchange.kanga.client.KangaRaterClient
import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.utils.reverse
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RatesService(
    private val kangaRaterClient: KangaRaterClient,
) {

    /**
     * [fromCurrency] [Currency]: oPLN
     * [toStableCoin] [StableCoin]: oEUR
     *
     */
    fun getRate(fromCurrency: String, toStableCoin: StableCoin): BigDecimal? {
        val rates = kangaRaterClient.getRates(fromCurrency, toStableCoin) ?: return null

        val rate = when (rates.cryptoCurrency) {
            // oEUR -> oPLN (sell) // 1 oEUR => 4.5 oPLN
            fromCurrency -> rates.sellRate
            // oPLN -> oEUR (buy + reverse) // 1 oEUR => 5.0 oPLN // 1 oPLN = 0.2 oEUR
            toStableCoin.name -> rates.buyRate.reverse(8)
            else -> null
        }

        return rate
    }
}