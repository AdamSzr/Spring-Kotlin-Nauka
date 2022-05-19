package exchange.kanga.domain.bon.cache

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import exchange.kanga.configuration.FolderConfiguration
import exchange.kanga.utils.common.Logger
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.math.BigDecimal
import javax.annotation.PostConstruct


@Component
//@DependsOn("FolderConfiguration")
class VoucherCurrenciesCache(
    private val folderConfiguration: FolderConfiguration,
) {
    private companion object : Logger

    internal val path = "src/main/resources/data/voucher-currencies.json"

    private var jsonObject: MutableSet<CurrencyData> = mutableSetOf()

    private var cacheSimple: MutableMap<String, MutableMap<String, BigDecimal>> = mutableMapOf()

    /**
     * get minimal deposit
     */
    fun getMinimalDeposit(currency: String, network: String): BigDecimal? =
        cacheSimple[currency]?.get(network)

    /**
     * refresh cache
     */
    fun refreshCacheFromJson() {
        load()
    }

    /**
     * load data on init
     */
    @PostConstruct
    private fun load() {
        try {
            cacheSimple = mutableMapOf()
            jsonObject = mutableSetOf()
            val set: MutableSet<CurrencyData> = jacksonObjectMapper().readValue(File(path))
            jsonObject = set
            set.forEach { currency ->
                currency.networks.forEach { network ->
                    cacheSimple.getOrPut(currency.symbol)
                    { mutableMapOf(network.name to network.deposit) }[network.name] =
                        network.deposit
                }
            }
            info("CACHE: $cacheSimple")
        } catch (e: IOException) {
            jsonObject = mutableSetOf()
            error("CACHE load from JSON: ${e.message}")
        }
    }

    /**
     * (backoffice) change data
     */
    internal fun addNewNetwork(currency: String, newNetwork: Networks) {
        val voucherCurrenciesItem = jsonObject.first { it.symbol == currency }

        val oldNetwork = voucherCurrenciesItem.networks.firstOrNull { it.name == newNetwork.name }

        if (oldNetwork == null) voucherCurrenciesItem.networks.add(newNetwork)
        else oldNetwork.deposit = newNetwork.deposit

        updateCache(currency, newNetwork.name, newNetwork.deposit)
    }

    internal fun updateOneNetwork(currency: String, network: String, deposit: BigDecimal) {
        val item = jsonObject.firstOrNull { it.symbol == currency }?.networks?.firstOrNull { it.name == network }
            ?: return
        item.deposit = deposit

        updateCache(currency, network, deposit)
    }

    /**
     * update data in cache
     */
    private fun updateCache(currency: String, network: String, deposit: BigDecimal) {
        updateInJson()
        updateInMap(currency, network, deposit)
    }

    private fun updateInMap(currency: String, network: String, deposit: BigDecimal) {
        cacheSimple.getOrPut(currency) { mutableMapOf(network to deposit) }[network] = deposit
    }

    private fun updateInJson() {
        try {
            jacksonObjectMapper().writeValue(File(path), jsonObject)
        } catch (e: IOException) {
            error("CACHE save to JSON: ${e.message}")
        }
    }

}

/**
 * models
 */
data class CurrencyData(
    val icon: String,
    val name: String,
    val networks: MutableSet<Networks>,
    val symbol: String                      // String
)

data class Networks(
    var deposit: BigDecimal,                // mutable
    val label: String,
    val name: String                        // String
)

