package exchange.kanga.domain.bon.cache

import exchange.kanga.domain.bon.model.CurrencyNetwork
import org.springframework.stereotype.Component

@Component
class CurrencyNetworkCache {

    val cache: MutableMap<String, CurrencyNetwork> = mutableMapOf()

    init {
        val list = listOf(
            CurrencyNetwork("BITCOIN", "Bitcoin", "https://www.blockchain.com/btc/address/", 3),
            CurrencyNetwork("ETHER", "Ethereum (ERC-20)", "https://etherscan.io/address/", 20),
            CurrencyNetwork("BSC", "BSC (BEP-20)", "https://bscscan.com/address/", 40),
            CurrencyNetwork("BTCV", "Bitcoin Vault", "", 0),
            CurrencyNetwork("RSK", "RSK", "", 0)
        )
        list.forEach { cache[it.name] = it }
    }

    fun add(currencyNetwork: CurrencyNetwork) {
        cache[currencyNetwork.name] = currencyNetwork
    }

    fun getAll() = cache.entries

    fun getByName(name: String) = cache[name]

    fun remove(currencyNetworkName: String) {
        cache.remove(currencyNetworkName)
    }
}