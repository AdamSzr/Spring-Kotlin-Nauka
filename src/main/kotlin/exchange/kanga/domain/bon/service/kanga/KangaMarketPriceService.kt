package exchange.kanga.domain.bon.service.kanga

import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.otc.integration.Integration
import exchange.kanga.otc.integration.cache.CacheManager
import exchange.kanga.otc.integration.requests.kanga.MarketPriceRequest
import exchange.kanga.otc.integration.responses.kanga.MarketPriceResponse
import exchange.kanga.otc.integration.utils.Pair
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration

@Service
class KangaMarketPriceService(
    private val kangaAuthService: KangaAuthService,
) {

    private val apiKey = kangaAuthService.apiKey

    private val cacheManager: CacheManager = CacheManager(Duration.ofMillis(1))

    /**
     * SELL -> 1 ETH -> 10.000 oPLN     rate: 10.000 ETH/oPLN
     * SELL -> 10 ETH -> 80.000 oPLN    rate: 8.000 ETH/oPLN
     */
    fun getPrice(fromCurrency: String, toStableCoin: StableCoin, amount: BigDecimal): kotlin.Pair<String, String> =
        kotlin.Pair(
            Integration.restExecutor()
                .cachedSend<MarketPriceResponse>(
                    getMarketPriceRequest(
                        TransactionType.SELL,
                        fromCurrency,
                        toStableCoin.name,
                        amount,
                        fromCurrency
                    ),
                    cacheManager
                ) { MarketPriceResponse::class.java }?.res() ?: "0.00",
            toStableCoin.name
        )

    private fun getMarketPriceRequest(
        type: TransactionType,
        currency: String,
        stable: String,
        amount: BigDecimal,
        inCurrency: String
    ): MarketPriceRequest {
        val req = when (type) {
            TransactionType.BUY -> MarketPriceRequest(
                apiKey,
                Pair(currency, stable),
                amount,
                inCurrency,
                false
            )
            TransactionType.SELL -> MarketPriceRequest(
                apiKey,
                Pair(stable, currency),
                amount,
                inCurrency,
                false
            )
        }
        println("Rate Pair: ${req.currency()} - inCurrency: $inCurrency - type: $type")
        return req
    }

    private enum class TransactionType { BUY, SELL }
}