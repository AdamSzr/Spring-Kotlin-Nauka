package exchange.kanga.domain.backoffice.wallet

import exchange.kanga.domain.bon.model.OperationType
import exchange.kanga.domain.bon.service.kanga.KangaTransferShiftService
import exchange.kanga.utils.toWallet
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AdminWalletService(
    private val kangaTransferShiftService: KangaTransferShiftService,
) {

    fun shift(from: String, to: String, amount: BigDecimal, currency: String) =
        kangaTransferShiftService.shift(
            OperationType.BACKOFFICE_SHIFT,
            from.toWallet(to),
            currency,
            amount,
        )
}