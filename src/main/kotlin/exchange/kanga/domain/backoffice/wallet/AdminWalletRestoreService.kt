package exchange.kanga.domain.backoffice.wallet

import exchange.kanga.domain.backoffice.voucher.AdminVoucherService
import exchange.kanga.domain.bon.model.OperationType
import exchange.kanga.domain.bon.model.TransferResponse
import exchange.kanga.domain.bon.response.InvalidPin
import exchange.kanga.domain.bon.response.VoucherNotExist
import exchange.kanga.domain.bon.service.kanga.KangaTransferShiftService
import exchange.kanga.domain.bon.service.kanga.KangaWalletService
import exchange.kanga.domain.bon.service.voucher.VoucherVerifyPinService
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.isNotZero
import exchange.kanga.utils.toWallet
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal

@Service
class AdminWalletRestoreService(
    private val kangaWalletService: KangaWalletService,
    private val kangaTransferShiftService: KangaTransferShiftService,

    private val voucherVerifyPinService: VoucherVerifyPinService,

    private val adminVoucherService: AdminVoucherService,
) {

    fun restore(fromId: String, toId: String, pin: String) =
        voucherVerifyPinService.getIfValidPin(fromId, pin)
            .flatMap { oldVoucher ->
                adminVoucherService.findById(toId)
                    .flatMap { newVoucher ->
                        moveWalletItemsToAnotherWallet(oldVoucher.getWalletName(), newVoucher.getWalletName())
                            .map { AdminWalletRestoreResponse(it) as Response }
                    }
                    .switchIfEmpty(VoucherNotExist().toMono())
            }
            .switchIfEmpty(InvalidPin().toMono())


    private fun moveWalletItemsToAnotherWallet(fromWallet: String, toWallet: String) =
        kangaWalletService.getBalances(fromWallet).values
            .toFlux()
            .filter { it.value.isNotZero() }
            .flatMap { item ->
                kangaTransferShiftService.shift(
                    OperationType.WALLET_RESTORE,
                    fromWallet.toWallet(toWallet),
                    item.currency,
                    item.value,
                    generateTitle(fromWallet, toWallet, item.currency, item.value)
                )
            }.collectList()


    private fun generateTitle(fromWallet: String, toWallet: String, currency: String, amount: BigDecimal) =
        "VOUCHER MIGRATION: from: $fromWallet to: $toWallet move: $amount $currency"

    data class AdminWalletRestoreResponse(val list: MutableList<TransferResponse>)
}