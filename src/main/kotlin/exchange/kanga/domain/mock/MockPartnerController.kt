package exchange.kanga.domain.mock

import exchange.kanga.domain.bon.listener.VoucherSignRequest
import exchange.kanga.domain.bon.listener.VoucherUpdateResponse
import exchange.kanga.utils.common.Logger
import exchange.kanga.utils.common.Response
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api-partner/")
class MockPartnerController {

    private companion object : Logger

    @PostMapping("/sign")
    fun sign(@RequestBody body: VoucherSignRequest): SignResponse {
        error("SIGN - id: ${body.id} - control: ${body.control}", false)
        return SignResponse(if (Math.random() < 0.5) "1234567890" else "0000000000")
    }

    @PostMapping("/change")
    fun handleChange(@RequestBody body: VoucherUpdateResponse): Response {
        error("CHANGE - id: ${body.id} - control: ${body.status}", false)
        return Response()
    }

    data class SignResponse(val signature: String)
}