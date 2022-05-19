package exchange.kanga.domain.cache.response

import exchange.kanga.utils.common.Response
import java.math.BigDecimal


sealed class FailureResponse(val code: Int, val error: String): Response("fail")

class AccountExistsFailureResponse: FailureResponse(1002, "account-exists")

class SignatureNotMatch: Response("fail")

class PartnerNotExist: FailureResponse(4001, "partner-not-exist")
class BadRequest: FailureResponse(4002, "bad-request")
class MinimalDepositFailure(val require: BigDecimal) : FailureResponse(4003, "minimal-deposit-failure")
class SendToBlockchainAddressWrongFormatFailureResponse: FailureResponse(4004, "send-to-blockchain-address-wrong-format-failure")
class FileNotExist: FailureResponse(4005, "file-not-exist")
class VoucherNotExist: FailureResponse(4006, "voucher-not-exist")
class InvalidPin: FailureResponse(4007, "invalid-pin")
class PartnerIdDuplicate: FailureResponse(4008, "partner-id-duplicate")
class ChangeStableToStableStepSellFailure: FailureResponse(4009, "change-stable-to-stable-step-sell-failure")
class ChangeStableToStableStepBuyFailure: FailureResponse(4010, "change-stable-to-stable-step-buy-failure")

//class ChangeStableToStableStepBuyFailureThrowable: Throwable()

class WalletAddressCreateFailureResponse: FailureResponse(2502, "wallet-address-create-fail")

class CurrencySellFailure: FailureResponse(4005, "currency-sell-failure")


class NoAuthorization: FailureResponse(9997, "no-authorization")
class LockProblem(val message: String?): FailureResponse(9996, "lock-problem")
class InvalidCredentials: FailureResponse(9995, "invalid-credentials")
class SerializeUnknownProblem( val message: String? = null): FailureResponse(9994, "serialize-problem")
