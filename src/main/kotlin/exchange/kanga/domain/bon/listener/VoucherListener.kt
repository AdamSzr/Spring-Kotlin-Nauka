package exchange.kanga.domain.bon.listener

import exchange.kanga.client.WebClient
import exchange.kanga.domain.bon.model.*
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.domain.bon.utils.HashUtils
import exchange.kanga.utils.common.Response
import org.springframework.context.event.EventListener
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class VoucherListener(
    private val webClient: WebClient,
    private val voucherRepository: VoucherRepository,
) {

    @EventListener
    fun notifyPartner(event: VoucherCreateEvent) =
        webClient
            // send notification
            .fetchData(
                event.partner.signUrl,
                VoucherSignResponse::class.java,
                VoucherSignRequest(event),
                HttpMethod.POST
            )
            // handle sign response
            .filter {
                when (it) {
                    is VoucherSignResponse -> {
                        validateSignatureFromPartner(event.partner, event.voucher, it.signature)
                    }
                    else -> false
                }
            }
            .flatMap { voucherRepository.save(event.voucher.changeState(State.SIGNED)) }
            .switchIfEmpty { voucherRepository.save(event.voucher.changeState(State.UNSIGNED)) }
            .doOnNext { updateVoucher(VoucherUpdateEvent(event.partner, it)) }

    @EventListener
    fun updateVoucher(event: VoucherUpdateEvent) =
        webClient
            .fetchData(
                event.partner.updateUrl,
                Any::class.java,
                VoucherUpdateResponse(
                    event.voucher,
                    HashUtils.getSignature(event.partner, event.voucher)
                ),
                HttpMethod.POST
            )
            .subscribe()

    private fun validateSignatureFromPartner(partner: Partner, voucher: Voucher, signature: String) =
        HashUtils.compare(partner, voucher, signature)
}

// create
data class VoucherCreateEvent(val partner: Partner, val voucher: Voucher)

// sign after create
data class VoucherSignRequest(
    val partner: String,
    val id: String,
    val pin: String,
    val currency: String,       // CurrencyData
    val type: VoucherType,
    val email: String,
    val kyc: Boolean,
    val control: String,
    val billingMode: BillingMode,
    val operator: Operator,
    val expected: List<String>
) {
    constructor(event: VoucherCreateEvent) : this(
        event.partner.id,
        event.voucher.id,
        event.voucher.pin,
        event.voucher.currency,
        event.voucher.type,
        event.voucher.email ?: "",
        event.voucher.kyc,
        event.voucher.control,
        event.partner.billingMode,
        event.voucher.operator,
        emptyList()
    )
}

data class VoucherSignResponse(val signature: String): Response()

// update
data class VoucherUpdateEvent(val partner: Partner, val voucher: Voucher)

// inform after update
data class VoucherUpdateResponse(
    val id: String,
    val status: State,
    val comment: String,
    val signature: String,
): Response() {
    constructor(voucher: Voucher, comment: String, signature: String) : this(
        voucher.id,
        voucher.state,
        comment,
        signature
    )

    constructor(voucher: Voucher, comment: String): this(
        voucher.id,
        voucher.state,
        comment,
        "removed-by-kanga"
    )
}