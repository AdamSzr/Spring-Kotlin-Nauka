package exchange.kanga.utils.common

import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ServiceRepository : ReactiveMongoRepository<Service, String> {
    @Query("{ 'serviceName':?0 }")
    fun find(serviceName: String): Mono<Service>
}
