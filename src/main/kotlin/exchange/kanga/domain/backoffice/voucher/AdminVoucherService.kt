package exchange.kanga.domain.backoffice.voucher

import exchange.kanga.domain.bon.cache.PartnerCache
import exchange.kanga.domain.bon.listener.VoucherUpdateEvent
import exchange.kanga.domain.bon.model.State
import exchange.kanga.domain.bon.model.VoucherType
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.domain.bon.response.PartnerNotExist
import exchange.kanga.domain.mock.MockData
import exchange.kanga.utils.common.Response
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class AdminVoucherService(
    private val appContext: ApplicationContext,
    private val repository: VoucherRepository,
    private val partnerCache: PartnerCache,
    private val mockData: MockData,
) {

    /**
     * Response
     */
    fun cancel(id: String, reason: String): Mono<Response> =
        repository.findById(id)
            .flatMap { voucher ->
                val partner = partnerCache.getPartner(voucher.partnerId)
                    ?: return@flatMap (PartnerNotExist()).toMono()

                repository.save(voucher.changeState(State.CANCELED, reason))
                    .doOnNext { appContext.publishEvent(VoucherUpdateEvent(partner, it)) }
                    .map { VoucherCancelResponse(it.id, it.state, reason) }
            }

    //TODO("try better filter")
    fun getBy(
        partner: List<String>? = null,
        state: List<State>? = null,
        type: List<VoucherType>? = null,
        kyc: Boolean? = null
    ) =
        repository.findAll()
            .filter {
                (partner.isNullOrEmpty() || partner.contains(it.partnerId))
                        && (state.isNullOrEmpty() || state.contains(it.state))
                        && (type.isNullOrEmpty() || type.contains(it.type))
                        && (kyc == null || kyc == it.kyc)
            }
            .collectList()
            .map { VoucherGetListResponse(it) }

    fun getAll() =
        repository.findAll()
            .collectList()
            .map { VoucherGetListResponse(it) }


    /**
     * Methods
     */
    fun findById(id: String) =
        repository.findById(id)

    /**
     * Generate mock - remove on prod
     */
    fun generateBy(
        partner: List<String>? = null,
        state: List<State>? = null,
        type: List<VoucherType>? = null,
        kyc: Boolean? = null
    ) =
        generate()
            .filter { partner.isNullOrEmpty() || partner.contains(it.partnerId) }
            .filter { state.isNullOrEmpty() || state.contains(it.state) }
            .filter { type.isNullOrEmpty() || type.contains(it.type) }
            .filter { kyc == null || kyc == it.kyc }


    fun generate() = mockData.generateVouchers(10000)
}