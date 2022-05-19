package exchange.kanga.structures

import exchange.kanga.utils.common.NoAuthorization
import exchange.kanga.utils.common.Response
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Instant
import javax.annotation.PostConstruct

@Document
data class ExampleModel(
    @Id val serviceName: String,
    val password: String,
    val items: List<String> = mutableListOf(),
    val ok: Boolean = false,
    val tag: String = "v1",
    val created: Instant = Instant.now(),
    val text: String = "",
)

//
//interface ExampleRepository : ReactiveMongoRepository<ExampleModel, String>
//{
//    @Query("{ 'password': ?0 }")
//    fun findByEmail(password: String): Mono<ExampleModel>
//
//    fun findByTag(tag: String):Flux<ExampleModel>
//    fun findByTagAndTextContainsIgnoreCase(tag: String, textContains:String):Flux<ExampleModel>
//    fun findByTagAndOkIsTrue (tag: String) :Flux<ExampleModel>
//    fun findByTagIn (tags: List<String>) :Flux<ExampleModel>
//    fun findDistinctByTagIn (tags: List<String>) :Flux<ExampleModel>
//}
//
//
//data class Transfer(
//    @Id val id: String = ObjectId().toHexString(),
//    val type: OperationType = OperationType.UNKNOWN,
//    val kind: TransferKind,
//    val amount: BigDecimal,
//    val currency: String,
//    val from: String,
//    val to: String,
//    val title: String? = null,
//    val date: Instant = Instant.now(),
//    var status: TransferStatus = TransferStatus.ACTIVE,
//    var code: Int = 0,
//)
//
//@Repository
//interface TransferRepository: ReactiveMongoRepository<Transfer, ObjectId> {
//
//    @Query("{'\$or': [{'to': ?0}, {'from': ?0}]}")
//    fun findAllByToOrFrom(nickname: String) : Flux<Transfer>
//
//    fun findAllByType(type: OperationType): Flux<Transfer>
//
//    fun findAllByTypeIn(types: List<OperationType>): Flux<Transfer>
//
//    fun findAllByOrderByDateDesc(): Flux<Transfer>
////    .findAll(Sort.by("date").descending())
//
//}
//
//@Document
//data class User(
//    @Id val nickname: String,
//    var password: String,
//    var role: UserRole = UserRole.USER,
//    var description: String? = null,
//    var details: Map<String, String> = mapOf(),
//    val devices: List<Device> = listOf(),
//    val consents: List<String> = listOf(),
//    val language: Language = Language.ENGLISH,
//    val recommending: String? = null,
//    var imageUrl: String? = null,
//    @DBRef val permissions: List<Permission> = listOf(),
//    var metaTags: List<String> = listOf(),
//    val created: Instant = Instant.now(),
//    var version: Byte = 0,
//    var lastLogin: Instant = Instant.now()
//)
//
//@Repository
//interface UserRepository: ReactiveMongoRepository<User, String> {
//
//    @Query("{ '_id' : {\$regex : /^?0$/ , \$options : 'i' } }")
//    fun findByNickname(nickname: String): Mono<User>
//
//    @Query("{ 'details.email': ?0 }")
//    fun findByEmail(email: String): Mono<User>
//
//    @Query("{ 'details.email': ?0 }")
//    fun findManyByEmail(email: String): Flux<User>
//
//    @Query("{'\$or': [{'details.email': ?0}, {'nickname': ?0}]}")
//    fun findByNicknameOrEmail(nicknameOrEmail: String): Mono<User>
//
//    @Query("{ '_id' : {\$regex : ?0 , \$options : 'i' } }")
//    fun findAllByNicknameContains(nickname: String): Flux<User>
//
//    @Query("{ 'details.phone': /?0/ }")
//    fun findManyByPhone(phone: String): Flux<User>
//
////    @Query("{ \$and : [ { created : { \$gte : ISODate(" + "?0" + ") } } , { created : { \$lte : ISODate(" + "?1" + ") } } ] }")
////    @Query("{ \$and : [ { created : { \$gte : ?0 } } , { created : { \$lte : ?1 } } ] }")
////    @Query("{ \$and : [ { created : { \$gte : { \$date : ?0 } } } , { created : { \$lte : { \$date : ?1 } } } ] }")
////    fun findAllByCreatedBetween(dateFrom: String, dateTo: String): Flux<User>
//
//    fun findAllByCreatedBetween(dateFrom: Instant, dateTo: Instant): Flux<User>
//    fun findAllByCreatedAfter(date: Instant): Flux<User>
//
//    //    { lastLogin : { $gt : ISODate('2021-10-20T00:00:00.000+00:00') } }
//    fun findAllByLastLoginBefore(date: Instant): Flux<User>
//
//    @Query("{ 'lastLogin' : { \$exists : ?0 } }")
//    fun findAllByLastLoginExist(boolean: Boolean): Flux<User>
//
//    @Query("{ 'details.email' : { \$exists : ?0 } }")
//    fun findAllActivated(boolean: Boolean): Flux<User>
//}
//
//
//@RestController()
//@RequestMapping(value = ["api/test"])
//class ExamlpleSvc(private val exmapleRepository: ExampleRepository) {
//    @PostConstruct
//    fun onInit() {
//
//        val x = mapOf<Int, String>(1 to "B", 2 to "C")
//        val x2: Map<Int, String> = mapOf(1 to "b")
//        val x3: Map<Int, String> = mapOf(1 to "c")
//        val x4: MutableMap<Int, String> = mutableMapOf(1 to "C")
//        x4.values.map( String::toUpperCase).toSortedSet{ o1,o2 -> o1.compareTo(o2) }
//        println(x3)
//
//
//        (1..100).toFlux().map { it -> ExampleModel(it.toString(), "passwd${it}") }
//            .map Models@ {it ->
//                exmapleRepository.save(it)
//                    .map{ it -> it.serviceName.uppercase() }
//                    .map Nested@{ it ->
//                        if (it.startsWith("A"))
//                            return@Nested "DONE"
//
//                        if (it.endsWith("B"))
//                            return@Nested "ENDS B"
//
//                        it
//                    }
//
//            }
//            .doOnNext { println(it) }
//            .subscribe()
//    }
//
//    @GetMapping
//    fun test() {
//
//        val te = exmapleRepository
//            .count()
//            .map { count ->
//                if (count > 10)
//                    return@map 0
//
//                println(count)
//                count
//            }
//
//        println(te.subscribe())
//        te.subscribe()
//    }
//
//    @GetMapping(value = ["sec"])
//    fun t2(): Mono<Long> {
//        return exmapleRepository.count()
//    }
//
//    @GetMapping(value = ["sec2"])
//    fun t3(): Flux<ExampleModel> {
//        return exmapleRepository.findAll()
//    }
//
//    @GetMapping(value = ["sec3"])
//    fun t4(): Mono<List<ExampleModel>> {
//        return t3().collectSortedList { name1, name2 -> name1.serviceName.compareTo(name2.serviceName, true ) }
//    }
//
//    @GetMapping(value = ["sec4"])
//    fun t5(): Mono<Response> {
//        return t3()
//            .filter { false }
//            .map { it.toString() }
//            .collectSortedList()
//            .map { ResponseList(it) as Response }
//            .defaultIfEmpty(NoAuthorization())
//
//    }
//
//    @GetMapping(value = ["sec5"])
//    fun t6(): Any {
//        val exm = ExampleModel("nameSvc", "somePass", tag = "v2")
//
//        return 1
//    }
////
////    @GetMapping(value = ["sec6"])
////    fun t7(): Any {
////         t3().collectMap({ it.serviceName },{it})
////             .map{ it -> it}
////    }
//
//    class ResponseList(val names: List<String>) : Response()
//}
