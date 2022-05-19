package exchange.kanga.domain.bon.repo

import exchange.kanga.domain.bon.model.State
import exchange.kanga.domain.bon.model.Voucher
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.Instant

@Repository
interface VoucherRepository: ReactiveMongoRepository<Voucher, String> {
    fun findAllByStateIn(stateList: List<State>): Flux<Voucher>
    fun findAllByState(state: State): Flux<Voucher>

    fun findAllByStateAndExpireBefore(state: State, expireBefore: Instant): Flux<Voucher>
}