package exchange.kanga.domain.bon.cache

import exchange.kanga.domain.bon.model.Partner
import exchange.kanga.domain.bon.repo.PartnerRepository
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PartnerCache(
    private val partnerRepository: PartnerRepository,
) {
    val partners = mutableMapOf<String, Partner>()

    fun getPartner(id: String) = partners[id]

    fun add(partner: Partner): Partner {
        partners[partner.id] = partner
        addToRepo(partner)
        return partner
    }

    private fun addToRepo(partner: Partner) {
        partnerRepository.save(partner).subscribe()
    }

    @PostConstruct
    private fun onInit() {
        partnerRepository.findAll()
            .doOnNext { partners[it.id] = it }
            .doFinally {  }
            .subscribe()
    }
}