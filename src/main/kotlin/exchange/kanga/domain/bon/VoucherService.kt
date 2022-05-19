package exchange.kanga.domain.bon

import exchange.kanga.client.KangaIntegrator
import exchange.kanga.domain.bon.cache.PartnerCache
import exchange.kanga.domain.bon.listener.VoucherCreateEvent
import exchange.kanga.domain.bon.listener.VoucherUpdateEvent
import exchange.kanga.domain.bon.model.*
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.domain.bon.reqres.*
import exchange.kanga.domain.bon.response.*
import exchange.kanga.domain.bon.service.MinimumDepositValidator
import exchange.kanga.domain.bon.service.voucher.VoucherCreateService
import exchange.kanga.domain.bon.utils.HashUtils
import exchange.kanga.domain.bon.utils.StringGeneratorUtils
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.common.roundToHourFloor
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.time.Instant


@Service
class VoucherService(
    private val appContext: ApplicationContext,
    private val partnerCache: PartnerCache,
    private val repo: VoucherRepository,
    private val voucherCreateService: VoucherCreateService,
) {

    private fun getPartner(id: String) = partnerCache.getPartner(id)

    fun create(body: VoucherCreateRequest): Response = voucherCreateService.create(body)

    fun get(id: String, signature: String?): Mono<Response> {
        if (signature.isNullOrBlank()) return SignatureNotMatch().toMono()

        var partner: Partner? = null

        return repo.findById(id)
            .filter {
                partner = getPartner(it.partnerId) ?: return@filter false
                val seedForSignature = HashUtils.getSeedForSignature(partner!!, it)
                val validate = HashUtils.compare(seedForSignature, partner!!.hashMethod, signature)
                validate
            }
            .map {
                if (it.type == VoucherType.WITHDRAW && partner?.billingMode == BillingMode.ON_CHAIN)
                    VoucherGetResponse(it.id, it.state, it.control, it.expire, it.address, it.network) as Response
                else
                    VoucherGetResponse(it.id, it.state, it.control, it.expire) as Response
            }
            .switchIfEmpty(SignatureNotMatch().toMono())
    }

    fun cancel(id: String, signature: String?): Mono<Response> =
        Mono.just(id)
            .filter { signature.isNullOrBlank() }
            .map { SignatureNotMatch() as Response }
            .switchIfEmpty {
                repo.findById(id)
                    .filter { voucher ->
                        val partner = getPartner(voucher.partnerId) ?: return@filter false
                        validateSignatureFromPartner(partner, voucher, signature!!)
                    }
                    .flatMap<Voucher?> { repo.save(it.changeState(State.CANCELED)) }
                    .doOnNext { appContext.publishEvent(VoucherUpdateEvent(getPartner(it.partnerId)!!, it)) }
                    .map<Response?> { (VoucherChangeResponse(it.id, it.state) as Response) }
                    .switchIfEmpty(SignatureNotMatch().toMono())
            }

    private fun validateSignatureFromPartner(partner: Partner, voucher: Voucher, signature: String)
        = HashUtils.compare(partner, voucher, signature)


}
