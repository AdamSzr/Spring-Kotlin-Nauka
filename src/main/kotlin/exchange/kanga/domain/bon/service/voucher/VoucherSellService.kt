package exchange.kanga.domain.bon.service.voucher

import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.domain.bon.model.Voucher
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.domain.bon.service.kanga.KangaOrderService
import exchange.kanga.domain.bon.service.kanga.KangaWalletService
import exchange.kanga.utils.common.Logger
import exchange.kanga.utils.isZero
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import javax.annotation.PostConstruct

@Service
class VoucherSellService(
    private val kangaOrderService: KangaOrderService,
    private val kangaWalletService: KangaWalletService,

    private val voucherRepository: VoucherRepository,
) {

    private companion object : Logger

    @PostConstruct
    fun onInit() {
//        val sell = sell(Currency.BTC, StableCoin.oPLN, BigDecimal("0.001"), "testJarek")
//        println("Sell: $sell")
    }

    fun sell(voucher: Voucher, toStableCoin: StableCoin, amount: BigDecimal, walletKey: String): BigDecimal? {

        val fromCurrency: String = voucher.currency

        var sell: BigDecimal? = null

        val stableBefore = kangaWalletService.getBalanceCurrencyValue(walletKey, toStableCoin.name)
//        error("Stable: $stableBefore", false)

        kangaOrderService.createOrder(fromCurrency, toStableCoin, amount, walletKey)

        // repeat after 100, 200, 300, 400, 500 ms => (max 1,5 sec time + Kanga response)
        // next every 0,5 sec (max 1,5 + 1 + 1,5 + 2 = 6 sec)
        val repeatTimeInMs = listOf<Long>(100, 200, 300, 400, 500, 1000, 1500, 2000)

        for (index in repeatTimeInMs.indices) {

            Thread.sleep(repeatTimeInMs[index])

            val wallet = kangaWalletService.getBalances(walletKey)
            val stableAfter = wallet[toStableCoin.name]?.value ?: BigDecimal.ZERO
            val lockedCurrency = wallet[fromCurrency]?.lockedValue ?: BigDecimal.ZERO

            info("Stable: $toStableCoin - $stableAfter")
            info("Currency: $fromCurrency - $lockedCurrency")

            val difference = stableAfter.minus(stableBefore)

            info(
                "Verify Order: ${index+1} - Locked: $lockedCurrency $fromCurrency - Stable: (before: $stableBefore, after: $stableAfter) $toStableCoin - Sell: $difference"
            )

            if (stableAfter != stableBefore && lockedCurrency.isZero()) {
                sell = difference
                val rate = difference.divide(amount, 2, RoundingMode.HALF_UP)
                error("SOLD: from: $amount $fromCurrency - to: $difference $toStableCoin - rate: $rate", false)
                voucherRepository.save(voucher.changeToStable(toStableCoin, rate)).subscribe()
                break
            }

        }

        return sell
    }


}