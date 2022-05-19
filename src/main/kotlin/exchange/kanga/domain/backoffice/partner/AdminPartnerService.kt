package exchange.kanga.domain.backoffice.partner

import exchange.kanga.domain.bon.cache.PartnerCache
import exchange.kanga.domain.bon.model.BillingMode
import exchange.kanga.domain.bon.model.Partner
import exchange.kanga.domain.bon.repo.PartnerRepository
import exchange.kanga.domain.bon.response.PartnerNotExist
import exchange.kanga.domain.mock.MockData
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.isValidEmail
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Service
class AdminPartnerService(
    private val partnerCache: PartnerCache,
    private val mockData: MockData,
    private val partnerRepository: PartnerRepository,
) {

    fun create(partner: Partner): Partner? {
        if (partnerCache.getPartner(partner.id) == null) return null
        validateBadRequestPartnerCreate(partner)
        partnerCache.add(partner)
        return partner
    }

    fun getById(partnerId: String) =
        findById(partnerId)
            .map { AdminPartnerResponse(it) as Response }
            .switchIfEmpty(PartnerNotExist().toMono())

    fun getAll() =
        findAll()
            .collectList()

    private fun findAll() = partnerRepository.findAll()

    fun findById(partnerId: String) =
        partnerRepository.findById(partnerId)

    fun generate() = mockData.generatePartners(100)

    private fun validateBadRequestPartnerCreate(partner: Partner) =
        partner.voucherExpireTime.toString().contains("-")
                || partner.password.isBlank()
                || partner.billingMode == BillingMode.ON_CHAIN
                || !partner.signUrl.contains("http")
                || !partner.updateUrl.contains("http")
                || !partner.walletName.isValidEmail()

    private fun tryParseStringToDuration(string: String?): Duration? {
        if (string == null) return Duration.ZERO
        if (string.contains("-")) return null
        return try {
            Duration.parse(string)
        } catch (e: Exception) {
            null
        }
    }

    data class AdminPartnerResponse(val partner: Partner): Response()
}