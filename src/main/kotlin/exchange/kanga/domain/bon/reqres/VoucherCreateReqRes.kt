package exchange.kanga.domain.bon.reqres

import com.fasterxml.jackson.annotation.JsonProperty
import exchange.kanga.domain.bon.model.*
import exchange.kanga.utils.common.Response
import java.math.BigDecimal
import java.time.Instant

/**
 * [billingMode] if null: get from partner default settings
 * [operator] if null: get ANY
 */
data class VoucherCreateRequest(
    val partner: String,
    val currency: String,               // CurrencyData
    val type: VoucherType,
    val control: String,
    // optional
    val email: String? = null,
    @JsonProperty("billing_mode")
    val billingMode: BillingMode? = null,
    val network: String? = null,        // CurrencyNetwork
    val kyc: Boolean = false,
    val operator: Operator = Operator.ANY,
    val amount: BigDecimal = BigDecimal.ZERO,
)

data class VoucherCreateResponse(
    val id: String,
    val address: String? = null,
    val network: String? = null,
    val expired: Instant? = null,
    val state: State = State.CREATED
) : Response()

data class VoucherCreateOffChainResponse(
    val id: String,
    val expired: Instant? = null,
    val state: State = State.CREATED
) : Response()
