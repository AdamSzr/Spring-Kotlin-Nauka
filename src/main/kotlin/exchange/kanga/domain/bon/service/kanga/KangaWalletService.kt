package exchange.kanga.domain.bon.service.kanga

import exchange.kanga.otc.integration.Integration
import exchange.kanga.otc.integration.requests.kanga.GetWalletsRequest
import exchange.kanga.otc.integration.responses.kanga.WalletsResponse
import exchange.kanga.otc.integration.wrappers.WalletsWrapper
import exchange.kanga.utils.common.Logger
import exchange.kanga.utils.isNotZero
import org.springframework.stereotype.Service
import scala.reflect.Manifest
import java.math.BigDecimal

@Service
class KangaWalletService {

    private companion object : Logger

    fun getBalanceCurrencyValue(walletKey: String, currency: String): BigDecimal =
        getBalances(walletKey)[currency]?.value ?: BigDecimal.ZERO

    fun getBalanceCurrencyLocked(walletKey: String, currency: String): BigDecimal =
        getBalances(walletKey)[currency]?.lockedValue ?: BigDecimal.ZERO

    fun getBalances(walletKey: String?): Map<String, WalletItem> {
        val wallets = mutableMapOf<String, WalletItem>()

        try {
            get(walletKey)
                .filter { it.value.value.isNotZero() }
                .forEach {
                    wallets[it.key] =
                        WalletItem(it.value.currency(), it.value.value(), it.value.lockedValue(), it.value.address())
                }
        } catch (e: ClassCastException) {
            logger().error("Wallet from Kanga problem : Null or Empty -> User: $walletKey - Message:  ${e.message}")
        } catch (e: Exception) {
            logger().error("Wallet from Kanga problem : Undefined -> User: $walletKey - Message:  ${e.message}")
        }

        return wallets
    }

    private fun get(walletKey: String?): WalletsWrapper =
        Integration.restExecutor().send(
            GetWalletsRequest(walletKey, null),
            WalletsWrapper(),
            Manifest { WalletsResponse::class.java }
        )

    data class WalletItem(
        val currency: String,
        val value: BigDecimal,
        val lockedValue: BigDecimal,
        val address: String?
    )
}


