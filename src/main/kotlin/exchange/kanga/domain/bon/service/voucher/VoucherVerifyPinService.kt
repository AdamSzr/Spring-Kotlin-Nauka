package exchange.kanga.domain.bon.service.voucher

import exchange.kanga.domain.bon.cache.PartnerCache
import exchange.kanga.domain.bon.model.Voucher
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.domain.bon.utils.HashUtils
import exchange.kanga.utils.common.Response
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class VoucherVerifyPinService(
    private val repo: VoucherRepository,
    private val partnerCache: PartnerCache,
) {

    /**
     * Get
     */
    fun getIfValidPin(id: String, pin: String): Mono<Voucher> =
        getVoucher(id)
            .filter { it.pin == pin }

    fun getIfValidHPinHash(id: String, pin: String): Mono<Voucher> =
        getVoucher(id)
            .filter { voucher ->
                val partner = getPartner(voucher.partnerId) ?: return@filter false
                HashUtils.compare(partner, voucher, pin)
            }

    /**
     * Check
     */
    fun check(id: String, pin: String): Mono<Boolean> =
        getVoucher(id)
            .map { it.pin == pin }

    fun checkAsResponse(id: String, pin: String): Mono<Response> =
        check(id, pin)
            .filter { it }
            .map { Response() }
            .switchIfEmpty { Response("fail").toMono() }

    fun checkHash(id: String, pin: String): Mono<Boolean> =
        getVoucher(id)
            .map { voucher ->
                val partner = getPartner(voucher.partnerId) ?: return@map false
                HashUtils.compare(partner, voucher, pin)
            }

    private fun getVoucher(id: String) = repo.findById(id)

    private fun getPartner(id: String) = partnerCache.getPartner(id)
}

