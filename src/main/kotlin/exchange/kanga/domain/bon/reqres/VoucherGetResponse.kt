package exchange.kanga.domain.bon.reqres

import com.fasterxml.jackson.annotation.JsonInclude
import exchange.kanga.domain.bon.model.State
import exchange.kanga.utils.common.Response
import java.time.Instant

/**
 * [address] & [network] only if (type == WITHDRAW && billing_mode == ON_CHAIN)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class VoucherGetResponse(
    val id: String,
    val status: State,
    val control: String?,
    val expired: Instant?,
    val address: String? = null,
    val network: String? = null,
): Response()