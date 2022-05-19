package exchange.kanga.domain.bon.repo

import exchange.kanga.domain.bon.model.OperationType
import exchange.kanga.domain.bon.model.Transfer
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TransferRepository : ReactiveMongoRepository<Transfer, ObjectId> {

    @Query("{'\$or': [{'to': ?0}, {'from': ?0}]}")
    fun findAllByToOrFrom(nickname: String): Flux<Transfer>

    fun findAllByType(type: OperationType): Flux<Transfer>

    fun findAllByTypeIn(types: List<OperationType>): Flux<Transfer>

    fun findAllByOrderByDateDesc(): Flux<Transfer>
}