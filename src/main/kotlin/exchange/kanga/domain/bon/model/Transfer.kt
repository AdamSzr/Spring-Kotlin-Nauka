package exchange.kanga.domain.bon.model

import com.fasterxml.jackson.annotation.JsonInclude
import exchange.kanga.utils.common.Response
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.math.BigDecimal
import java.time.Instant

data class TransferResponse(val transfer: Transfer) : Response()

data class Transfer(
    @Id val id: String = ObjectId().toHexString(),
    val type: OperationType = OperationType.UNKNOWN,
    val kind: TransferKind,
    val amount: BigDecimal,
    val currency: String,
    val from: String,
    val to: String,
    val title: String? = null,
    val date: Instant = Instant.now(),
    var status: TransferStatus = TransferStatus.ACTIVE,
    var code: Int = 0,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val network: String? = null,        // CurrencyNetwork
)


enum class TransferKind {
    TRANSFER, SHIFT, BLOCKCHAIN
}

enum class TransferStatus {
    ACTIVE, DONE, FAILURE
}

enum class OperationType(val label: String) {
    UNKNOWN("none"),

    BLOCKCHAIN_WITHDRAW("blockchain-withdraw"),

    SELL_INSIDE("sell-inside"),
    SELL_INSIDE_BACK("sell-inside-back"),
    BUY_INSIDE("buy-inside"),

    REALIZE("realize"),

    WALLET_RESTORE("wallet-restore"),
    BACKOFFICE_SHIFT("backoffice-shift")
    ;

    fun failureLabel(): String = "$label-failure"
    fun getTitle(id: String): String = "${label.replace("-", " ")} [$id]"
}