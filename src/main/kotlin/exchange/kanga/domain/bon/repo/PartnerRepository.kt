package exchange.kanga.domain.bon.repo

import exchange.kanga.domain.bon.model.Partner
import exchange.kanga.domain.bon.model.State
import exchange.kanga.domain.bon.model.Voucher
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PartnerRepository: ReactiveMongoRepository<Partner, String> {

}