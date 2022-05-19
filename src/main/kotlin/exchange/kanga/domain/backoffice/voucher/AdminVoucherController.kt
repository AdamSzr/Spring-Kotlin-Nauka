package exchange.kanga.domain.backoffice.voucher

import exchange.kanga.domain.bon.model.State
import exchange.kanga.domain.bon.model.Voucher
import exchange.kanga.domain.bon.model.VoucherType
import exchange.kanga.domain.bon.response.VoucherNotExist
import exchange.kanga.utils.common.NullAuthentication
import exchange.kanga.utils.common.Response
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/api-admin/voucher")
class AdminVoucherController(
    private val adminVoucherService: AdminVoucherService,
) {

    @GetMapping("/{id}")
    fun getById(
        authentication: Authentication?,
        @PathVariable id: String,
    ) =
        if (authentication == null) NullAuthentication().toMono()
        else
            adminVoucherService.findById(id)
                .map { VoucherGetResponse(it) as Response }
                .defaultIfEmpty(VoucherNotExist())

    @GetMapping()
    fun getBy(
        authentication: Authentication?,
        @RequestParam partner: List<String>?,
        @RequestParam state: List<State>?,
        @RequestParam type: List<VoucherType>?,
        @RequestParam kyc: Boolean?,
    ) =
        if (authentication == null) NullAuthentication().toMono()
        else
            adminVoucherService
                .getBy(partner, state, type, kyc)

    @DeleteMapping
    fun cancel(
        authentication: Authentication?,
        @RequestBody body: VoucherCancelRequest,
    ) =
        if (authentication == null) NullAuthentication().toMono()
        else adminVoucherService.cancel(body.id, body.reason)
}

data class VoucherCancelRequest(val id: String, val reason: String)
data class VoucherCancelResponse(val id: String, val state: State, val reason: String) : Response()

data class VoucherGetListResponse(val list: List<Voucher>) : Response()
data class VoucherGetResponse(val voucher: Voucher) : Response()

