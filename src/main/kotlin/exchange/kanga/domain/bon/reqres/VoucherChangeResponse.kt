package exchange.kanga.domain.bon.reqres

import exchange.kanga.domain.bon.model.State
import exchange.kanga.utils.common.Response

data class VoucherChangeResponse(
    val id: String,
    val status: State,
): Response()
