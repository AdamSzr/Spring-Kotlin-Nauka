package exchange.kanga.domain.bon.service.voucher

import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.domain.bon.model.TransferResponse
import exchange.kanga.domain.bon.model.Voucher
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.domain.bon.response.CurrencySellFailure
import exchange.kanga.domain.bon.response.FailureResponse
import exchange.kanga.domain.bon.service.kanga.KangaMarketPriceService
import exchange.kanga.domain.bon.service.kanga.KangaStableChangeByShiftService
import exchange.kanga.domain.bon.service.kanga.KangaWalletService
import exchange.kanga.domain.bon.service.partner.PartnerSettlementService
import exchange.kanga.utils.common.Logger
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.isNotZero
import exchange.kanga.utils.isZero
import org.springframework.http.server.ServerHttpRequest
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.math.sumAsBigDecimal
import java.math.BigDecimal

@Service
class VoucherRealizeService(
    private val kangaMarketPriceService: KangaMarketPriceService,
    private val kangaWalletService: KangaWalletService,
    private val kangaStableChangeByShiftService: KangaStableChangeByShiftService,

    private val sellService: VoucherSellService,
    private val checkPinService: VoucherVerifyPinService,

    private val partnerSettlementService: PartnerSettlementService,

    private val voucherRepository: VoucherRepository,
) {

    private companion object : Logger

    // 1 ETH +/- 12.520 oPLN
    fun calculate(id: String, toStableCoin: StableCoin) =
        getVoucher(id)
            .map {
                val amount = kangaWalletService.getBalanceCurrencyValue(it.getWalletName(), it.currency)
                kangaMarketPriceService.getPrice(it.currency, toStableCoin, amount)
            }


    // ok,fail
    fun verifyPin(id: String, pin: String) =
        checkPinService.checkAsResponse(id, pin)


    // 1 ETH -> 12.520 oPLN
    fun sell(id: String, toStableCoin: StableCoin) =
        getVoucher(id)
            .map {
                val amount = kangaWalletService.getBalanceCurrencyValue(it.getWalletName(), it.currency)

                val sell = sellService.sell(it, toStableCoin, amount, it.getWalletName())
                    ?: return@map CurrencySellFailure()

                if (sell.isZero()) return@map CurrencySellFailure()

                VoucherExchangeResponse(id, sell, toStableCoin)
            }
    data class VoucherExchangeResponse(val id: String, val amount: BigDecimal, val currency: StableCoin)

    // 1 ETH -> 12.520 oPLN
    fun realize(id: String, toStableCoin: StableCoin) =
        getVoucher(id)
            .map {
                val amount = kangaWalletService.getBalances(it.getWalletName())

                val amountStable = amount[toStableCoin.name]?.value ?: BigDecimal.ZERO
                val amountCurrencyToSell = amount[it.currency]?.value ?: BigDecimal.ZERO

                val listOfStableAndCurrency = listOf(toStableCoin.name, it.currency)
                val amountCurrencyToConvert = amount.entries.filter { item -> !listOfStableAndCurrency.contains(item.key) }.toList()

                val sell: BigDecimal = when {
                    (amount.size == 1 && amountStable.isNotZero()) ->
                            BigDecimal.ZERO
                    (amount.size == 1 && amountCurrencyToSell.isNotZero()) ->
                            sellService.sell(it, toStableCoin, amountCurrencyToSell, it.getWalletName())
                    (amount.size == 2 && amountStable.isNotZero() && amountCurrencyToSell.isNotZero()) ->
                            sellService.sell(it, toStableCoin, amountCurrencyToSell, it.getWalletName())
                    (amountCurrencyToConvert.isNotEmpty()) -> {
                        //TODO("wallet: [ETH, oPLN] realize: oEUR, steps: [sell ETH->oEUR, sell oPLN->oEUR")
                        val sell = sellService.sell(it, toStableCoin, amountCurrencyToSell, it.getWalletName()) ?: BigDecimal.ZERO

                        val sum = amountCurrencyToConvert.toFlux()
                            .flatMap { walletItem ->
                                kangaStableChangeByShiftService.changeStableToStable(
                                    fromStableCoin = StableCoin.valueOf(walletItem.value.currency),
                                    toStableCoin = toStableCoin,
                                    amount = walletItem.value.value,
                                    walletName = it.getWalletName()
                                )
                            }
                            .concatMap {
                                if (it is FailureResponse)
                                    Mono.error<BigDecimal>(it)
                                else
                                    Mono.just((it as KangaStableChangeByShiftService.ChangeStableToStableResponse).amount)
                            }
                            .defaultIfEmpty(BigDecimal.ZERO)
                            .sumAsBigDecimal()
                            .map { sum -> sum.plus(sell ?: BigDecimal.ZERO) }

                            sum.block()
                    }
                    else -> {
                        error("Voucher Realize: id: ${it.id} - stable: $toStableCoin currency: ${it.currency} - wallet: $amount")
                        BigDecimal.ZERO
                    }
                } ?: return@map CurrencySellFailure()

                if (amountStable.isZero() && sell.isZero()) return@map CurrencySellFailure()

                val amountToSend = amountStable.plus(sell)


                VoucherRealizeResponse(id, amountToSend, toStableCoin.name) as Response
//                settlePartner(it, amountToSend, toStableCoin)
//                    .map { response ->
//                        if (response.result == "ok" && response is TransferResponse)
//                            VoucherRealizeResponse(id, response.transfer.amount, response.transfer.currency)
//                        else response
//                    }
            }
            .doOnError { error(it.toString()) }
            .onErrorResume {
                if (it is FailureResponse)
                    (it as FailureResponse).toMono()
                else
                    Mono.empty<Response>()
            }

    data class VoucherRealizeResponse(val id: String, val amount: BigDecimal, val code: String): Response()

    private fun settlePartner(voucher: Voucher, amount: BigDecimal, stableCoin: StableCoin): Mono<TransferResponse> =
        partnerSettlementService.settle(voucher.partnerId, voucher.id, amount, stableCoin)


    //
    fun amountInVoucher(id: String) =
        getVoucher(id)
            .map { voucher ->
                kangaWalletService.getBalances(voucher.getWalletName()).map { WalletItemSimple(it.key, it.value.value) }
            }
            .map { VoucherWalletResponse(id, it) }
            .switchIfEmpty(VoucherWalletResponse(id, emptyList()).toMono())


    data class VoucherWalletResponse(val id: String, val wallet: List<WalletItemSimple> = emptyList()): Response()
    data class WalletItemSimple(val code: String, val amount: BigDecimal)

    private fun getVoucher(id: String) =
        voucherRepository.findById(id)

}

@Controller
@RestController("/api/v2/voucher")
class VoucherRealizeController(
    private val voucherRealizeService: VoucherRealizeService,
) {

    private companion object : Logger

    @PostMapping("/realize/{id}/{toStableCoin}")
    @CrossOrigin(origins = ["http://localhost:8085", "otc.kanga.dev", "otc.kanga.exchange"])
    fun realize(
        request: ServerHttpRequest,
        @RequestBody body: Any,
        @PathVariable id: String,
        @PathVariable toStableCoin: StableCoin,
    ): Mono<Response> {
        logRequest(
            endpoint = "GET:/api/v2/voucher/realize",
            host = "(remote: ${request.remoteAddress} local: ${request.localAddress})",
            header = request.headers.toSingleValueMap(),
            body = body
        )
        return voucherRealizeService.realize(id, toStableCoin)
    }

    @PostMapping("/pin/{id}/{hashPin}")
    fun verifyPin(@PathVariable id: String, @PathVariable hashPin: String) =
        voucherRealizeService.verifyPin(id, hashPin)

    @PostMapping("/sell/{id}/{toStableCoin}")
    fun sell(
        @PathVariable id: String,
        @PathVariable toStableCoin: StableCoin,
    ) =
        voucherRealizeService.sell(id, toStableCoin)

    @PostMapping("/check/{id}")
    fun checkAmount(@PathVariable id: String) =
        voucherRealizeService.amountInVoucher(id)
}