package exchange.kanga.domain.backoffice.transfer

import exchange.kanga.domain.bon.model.Transfer
import exchange.kanga.utils.common.NullAuthentication
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import reactor.kotlin.core.publisher.toMono

@Controller("/api-admin/transfer")
class AdminTransferController(
    private val adminTransferService: AdminTransferService,
) {

    @GetMapping
    fun getAll(authentication: Authentication?) =
        if (authentication == null) NullAuthentication().toMono() else
            adminTransferService.getAll()
}

data class AdminTransferListResponse(val list: MutableList<Transfer>)

