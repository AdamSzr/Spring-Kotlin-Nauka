package exchange.kanga.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.utils.common.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import javax.annotation.PostConstruct

@Service
class KangaRaterClient(
    private val unirestClient: UnirestClient,
) {

    @Value("\${api.service.rates.kanga}")
    private lateinit var url: String

//    @PostConstruct
//    fun onInit() {
//        println(getRates(Currency.oPLN, StableCoin.oEUR))
//        println(getRates(Currency.oPLN, StableCoin.oEUR))
//        println(getRates(Currency.oPLN, StableCoin.oEUR))
//    }

    internal fun getRates(fromCurrency: String, toStableCoin: StableCoin): Rate? {
        if (fromCurrency == toStableCoin.name)
            return Rate(cryptoCurrency = fromCurrency, fiatCurrency = toStableCoin.name)

        val currenciesToHandle = listOf(fromCurrency, toStableCoin.name)

        val response = getClient()
            .get(url)

        val responseNode = response.asJson()

        val responseObject = jacksonObjectMapper().readValue<SystemRatesWithFeeResponse>(responseNode.body.toString())

        val rates = responseObject.exchange
            ?.filter { currenciesToHandle.contains(it.cryptoCurrency) }
            ?.firstOrNull {
                // from: oEUR, to: oPLN // crypto: oEUR, fiat: PLN
                (it.cryptoCurrency == fromCurrency && it.fiatCurrency == convertStableToFiat(toStableCoin)) ||
                        // from: oPLN, to: oEUR // crypto: oEUR, fiat: PLN
                        it.cryptoCurrency == toStableCoin.name && it.fiatCurrency == convertCurrencyToFiat(fromCurrency)
            }
            ?: return null

        return rates
    }

    private fun getClient() =
        unirestClient.getClient()

    private fun convertCurrencyToFiat(currency: String) =
        try {
            val stable = StableCoin.valueOf(currency)
            val fiat = convertStableToFiat(stable)
            fiat
        } catch (e: Exception) {
            ""
        }

    private fun convertStableToFiat(stableCoin: StableCoin) =
        when (stableCoin) {
            StableCoin.oPLN -> "PLN"
            StableCoin.oEUR -> "EUR"
        }

    private data class SystemRatesWithFeeResponse(
        var atm: List<Rate>?,
        var exchange: List<Rate>?,
        var standard: List<Rate>?,
    ) : Response()

    data class Rate(
        val cryptoCurrency: String,
        val fiatCurrency: String,
        var buyRate: BigDecimal = BigDecimal.ONE,
        var sellRate: BigDecimal = BigDecimal.ONE,
    )
}