package exchange.kanga.domain.backoffice.wallet

import exchange.kanga.domain.backoffice.partner.AdminPartnerService
import exchange.kanga.domain.backoffice.voucher.AdminVoucherService
import exchange.kanga.domain.bon.response.VoucherNotExist
import exchange.kanga.domain.bon.service.kanga.KangaWalletService
import exchange.kanga.utils.common.NullAuthentication
import exchange.kanga.utils.common.Response
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal


@RestController
@RequestMapping("/api-admin/wallet")
class AdminWalletController(
    private val kangaWalletService: KangaWalletService,

    private val adminWalletService: AdminWalletService,
    private val adminVoucherService: AdminVoucherService,
    private val adminPartnerService: AdminPartnerService,
    private val adminWalletRestoreService: AdminWalletRestoreService,
) {

    @GetMapping
    fun getWallet(authentication: Authentication?): Response =
        if (authentication == null) NullAuthentication() else
            AdminWalletResponse(kangaWalletService.getBalances(null).values)

    @GetMapping("/{walletName}")
    fun getByWalletName(
        authentication: Authentication?,
        @PathVariable walletName: String,
    ): Response =
        if (authentication == null) NullAuthentication() else
            (AdminWalletResponse(getBalanceByWalletName(walletName)) as Response)

    @GetMapping("/voucher/{id}")
    fun getByVoucherId(
        authentication: Authentication?,
        @PathVariable id: String,
    ): Mono<Response> =
        if (authentication == null) NullAuthentication().toMono() else
            adminVoucherService.findById(id)
                .map { getBalanceByWalletName(it.getWalletName()) }
                .map { AdminWalletResponse(it) as Response }
                .defaultIfEmpty(VoucherNotExist())

    @GetMapping("/partner/{id}")
    fun getByPartnerId(
        authentication: Authentication?,
        @PathVariable id: String,
    ): Mono<Response> =
        if (authentication == null) NullAuthentication().toMono() else
            adminPartnerService.findById(id)
                .map { getBalanceByWalletName(it.walletName) }
                .map { AdminWalletResponse(it) as Response }
                .defaultIfEmpty(VoucherNotExist())

    @PostMapping("/restore")
    fun restore(
        authentication: Authentication?,
        @RequestBody body: AdminWalletRestoreRequest,
    ): Mono<Response> =
        if (authentication == null) NullAuthentication().toMono() else
            adminWalletRestoreService.restore(body.fromId, body.toId, body.pin)

    @PostMapping("/send/shift")
    fun send(
        authentication: Authentication?,
        @RequestBody body: AdminWalletShiftRequest,
    ) =
        adminWalletService.shift(body.from, body.to, body.amount, body.currency)

    private fun getBalanceByWalletName(walletName: String) =
        kangaWalletService.getBalances(walletName).values

    data class AdminWalletResponse(val list: Collection<KangaWalletService.WalletItem>) : Response()


    data class AdminWalletRestoreRequest(
        val fromId: String,
        val toId: String,
        val pin: String
    )

    data class AdminWalletShiftRequest(
        val from: String,
        val to: String,
        val amount: BigDecimal,
        val currency: String,
    )
}

