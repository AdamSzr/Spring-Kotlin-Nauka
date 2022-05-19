package exchange.kanga.domain.bon.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document
data class Voucher(
    @Id val id: String,
    val pin: String,                // PIN sam robie
    val partnerId: String,
    val type: VoucherType,
    var currency: String,           // CurrencyData
    var network: String? = null,    // CurrencyNetwork
    val address: String? = null,    // null?
    val operator: Operator = Operator.ANY,
    var expire: Instant? = null,
    var state: State = State.CREATED,
    val history: MutableList<VoucherHistory> = mutableListOf(VoucherHistory()),
    val email: String?,
    val kyc: Boolean = false,
    val control: String,
) {
    fun changeState(newState: State, comment: String = "") =
        this.apply {
            history.add(VoucherHistory(newState, comment = comment))
            state = newState
        }

    fun setExpireDate(date: Instant) =
        this.apply { expire = date }

    fun getWalletName() =
        "Voucher:${this.history.first().time}:${this.id}"

    fun changeToStable(stable: StableCoin, rate: BigDecimal) =
        this.apply {
            history.add(
                VoucherHistory(
                    state = history.last().state,
                    comment = "FROM: ${this.currency} ${this.network} TO: $stable - RATE:"
                )
            )
            this.currency = stable.name
            this.network = null
        }
}

//enum class Currency { oPLN, oEUR, BTC, ETH, USDT, USDC }
enum class VoucherType { DEPOSIT, WITHDRAW }
enum class BillingMode { ON_CHAIN, OFF_CHAIN }
//enum class Network { BITCOIN, ETHER, BSC, NONE }
enum class Operator { ANY, ATM }



/**
Utworzony | CREATED
Bon został utworzony. Do chwili podpisania przez partnera jest on bezużyteczny. Jest on usuwany po 3 dniach od utworzenia.
Podpisany | SIGNED
Udało się poprawnie zweryfikować dany bon poprzez komunikację z partnerem. Bon jest gotowy do użytku.
Niepodpisany | UNSIGNED
Nie powiodło się podpisanie bonu. Bon staje się nieważny i jest usuwany po 7 dniach od utworzenia.
Użyty | USED
Bon został użyty do wpłaty lub wypłaty środków
Wygasły | EXPIRED
Okres ważności bonu się zakończył. Jest on usuwany po 7 dniach od wygaśnięcia.
Anulowany | CANCELED
Bon został anulowany przez administrację lub partnera poprzez API. Jest on usuwany po 7 dniach od anulowania.
 */
enum class State { CREATED, SIGNED, UNSIGNED, USED, EXPIRED, CANCELED, FAILED }

data class VoucherHistory(
    val state: State = State.CREATED,
    val time: Instant = Instant.now(),
    val comment: String = "",
)