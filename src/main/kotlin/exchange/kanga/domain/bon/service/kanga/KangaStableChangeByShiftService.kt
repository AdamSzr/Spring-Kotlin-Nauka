package exchange.kanga.domain.bon.service.kanga

import exchange.kanga.domain.bon.model.*
import exchange.kanga.domain.bon.response.ChangeStableToStableStepBuyFailure
import exchange.kanga.domain.bon.response.ChangeStableToStableStepSellFailure
import exchange.kanga.domain.bon.service.RatesService
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.toWallet
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal

@Service
class KangaStableChangeByShiftService(
    private val ratesService: RatesService,
    private val kangaTransferShiftService: KangaTransferShiftService,
) {

    fun changeStableToStable(
        fromStableCoin: StableCoin,
        toStableCoin: StableCoin,
        walletName: String,
        amount: BigDecimal
    ): Mono<Response> {

        val rate = ratesService.getRate(fromStableCoin.name, toStableCoin)
            ?: return Mono.empty()

        val amountToSend = amount.multiply(rate)

        return kangaTransferShiftService.shift(
            OperationType.SELL_INSIDE,
            walletName.toWallet(""),
            fromStableCoin.name,
            amount,
            getTitle("SELL", amount, fromStableCoin, amountToSend, toStableCoin, rate)
        )
            .flatMap { transferSell ->

                if (transferSell.transfer.status != TransferStatus.DONE)
                    return@flatMap handleTransferSellProblem().toMono()

                kangaTransferShiftService.shift(
                    OperationType.BUY_INSIDE,
                    "".toWallet(walletName),
                    toStableCoin.name,
                    amountToSend,
                    getTitle("BUY", amount, fromStableCoin, amountToSend, toStableCoin, rate),
                )
                    .map { transferBuy ->

                        if (transferBuy.transfer.status != TransferStatus.DONE)
                            return@map handleTransferBuyProblem(transferBuy.transfer, walletName)

                        ChangeStableToStableResponse(amountToSend)
                    }
            }
    }

    private fun getTitle(
        type: String,
        amount: BigDecimal,
        fromStableCoin: StableCoin,
        amountToSend: BigDecimal,
        toStableCoin: StableCoin,
        rate: BigDecimal
    ) = "ChangeStable ($type) sell: ${amount.setScale(0)} $fromStableCoin buy: $amountToSend $toStableCoin rate: ${
        rate.setScale(4)
    }"

    private fun handleTransferSellProblem(): ChangeStableToStableStepSellFailure {
        return ChangeStableToStableStepSellFailure()
    }

    private fun handleTransferBuyProblem(transfer: Transfer, walletName: String): ChangeStableToStableStepBuyFailure {
        kangaTransferShiftService.shift(
            OperationType.SELL_INSIDE_BACK,
            "".toWallet(walletName),
            transfer.currency,
            transfer.amount,
            "ChangeStable (SELL)(BACK) id: ${transfer.id} code: ${transfer.code} transfer: $transfer"
        )

        return ChangeStableToStableStepBuyFailure()
    }

    data class ChangeStableToStableResponse(val amount: BigDecimal) : Response()
}