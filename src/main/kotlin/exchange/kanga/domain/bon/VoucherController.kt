package exchange.kanga.domain.bon

import exchange.kanga.domain.bon.reqres.VoucherCreateRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/voucher")
class VoucherController(
    private val voucherService: VoucherService,
) {

    @PutMapping
    fun create(
        @RequestBody body: VoucherCreateRequest,
        @RequestHeader headers: Map<String, String>,
    ) = voucherService.create(body)

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: String,
        @RequestHeader headers: Map<String, String>
    ) = voucherService.get(id, headers["signature"])

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String,
        @RequestHeader(name = "signature") signature: String?,
    ) = voucherService.cancel(id, signature)
}
