package exchange.kanga.domain.backoffice.partner

import exchange.kanga.domain.bon.model.Partner
import exchange.kanga.domain.bon.response.PartnerIdDuplicate
import exchange.kanga.utils.common.NullAuthentication
import exchange.kanga.utils.common.Response
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/api-admin/partner")
class AdminPartnerController(
    private val adminPartnerService: AdminPartnerService,
) {

    @PostMapping
    fun create(
        authentication: Authentication?,
        @RequestBody partnerToCreate: Partner,
    ): Response =
        if (authentication == null) NullAuthentication() else {
            val partner = adminPartnerService.create(partnerToCreate)
            if (partner == null) PartnerIdDuplicate() else PartnerCreateResponse(partner) as Response
        }

    @GetMapping
    fun getAll(authentication: Authentication?) =
        if (authentication == null) NullAuthentication().toMono() else
            adminPartnerService.getAll().map { PartnerGetListResponse(it) }

    @GetMapping("/{id}")
    fun getById(
        authentication: Authentication?,
        @PathVariable id: String,
    ) =
        if (authentication == null) NullAuthentication().toMono() else
            adminPartnerService.getById(id)


    @PutMapping
    fun update() {
        TODO("backoffice: partner update")
    }

    data class PartnerCreateResponse(val partner: Partner) : Response()
    data class PartnerGetListResponse(val list: List<Partner>) : Response()
}

