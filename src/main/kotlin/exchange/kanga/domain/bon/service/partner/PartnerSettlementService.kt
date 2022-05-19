package exchange.kanga.domain.bon.service.partner

import exchange.kanga.domain.bon.model.BillingMode
import exchange.kanga.domain.bon.model.OperationType
import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.domain.bon.model.TransferResponse
import exchange.kanga.domain.bon.repo.PartnerRepository
import exchange.kanga.domain.bon.service.kanga.KangaTransferBlockchainService
import exchange.kanga.domain.bon.service.kanga.KangaTransferShiftService
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.toWallet
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class PartnerSettlementService(
    private val kangaTransferShiftService: KangaTransferShiftService,
    private val kangaTransferBlockchainService: KangaTransferBlockchainService,

    private val partnerRepository: PartnerRepository,
) {

    // on-chain, off-chain
    val fee = 1.0

    fun settle(partnerId: String, voucherId: String, amount: BigDecimal, stableCoin: StableCoin) =
        partnerRepository.findById(partnerId)
            .flatMap { settle(it.billingMode, voucherId, amount, stableCoin, it.walletName) }

    private fun settle(
        billingMode: BillingMode,
        voucherId: String,
        amount: BigDecimal,
        stableCoin: StableCoin,
        partnerWalletName: String
    ): Mono<TransferResponse> =
        when (billingMode) {
            BillingMode.ON_CHAIN -> settleOnChain(voucherId, amount, stableCoin, partnerWalletName)
            BillingMode.OFF_CHAIN -> settleOffChain(voucherId, amount, stableCoin, partnerWalletName)
        }

    private fun settleOffChain(voucherId: String, amount: BigDecimal, stableCoin: StableCoin, partnerWalletName: String) =
        kangaTransferShiftService.shift(
            OperationType.BLOCKCHAIN_WITHDRAW,
            "".toWallet(partnerWalletName),
            stableCoin.name,
            calculateAmountToPartner(amount),
            generateTitle(voucherId),
        )

    private fun settleOnChain(voucherId: String, amount: BigDecimal, stableCoin: StableCoin, partnerWalletName: String) =
        kangaTransferBlockchainService.blockChain(
            OperationType.BLOCKCHAIN_WITHDRAW,
            "".toWallet(partnerWalletName),
            stableCoin.name,
            calculateAmountToPartner(amount),
            generateTitle(voucherId),
        )

    private fun calculateAmountToPartner(amount: BigDecimal) =
        amount.multiply(BigDecimal.ONE.minus((100.0 - fee).toBigDecimal()))

    private fun generateTitle(voucherId: String) =
        "Voucher: $voucherId"
}