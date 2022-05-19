package exchange.kanga.domain.bon.model

import exchange.kanga.domain.bon.utils.HashMethod
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Duration

@Document
data class Partner(
    @Id val id: String,
    val password: String,
    val billingMode: BillingMode,
    val signUrl: String,
    val updateUrl: String,
    val voucherExpireTime: Duration? = null,
    val hashMethod: HashMethod,
    val walletName: String
)

