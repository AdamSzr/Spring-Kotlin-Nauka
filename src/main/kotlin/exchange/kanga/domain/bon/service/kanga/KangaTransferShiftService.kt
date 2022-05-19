package exchange.kanga.domain.bon.service.kanga

import exchange.kanga.domain.bon.model.*
import exchange.kanga.domain.bon.repo.TransferRepository
import exchange.kanga.otc.integration.Integration
import exchange.kanga.otc.integration.requests.kanga.WalletShiftRequest
import exchange.kanga.otc.integration.responses.FailureRestResponse
import exchange.kanga.otc.integration.responses.RestResponseCallback
import exchange.kanga.otc.integration.responses.SuccessResponse
import exchange.kanga.otc.integration.utils.Pair
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import scala.Option
import scala.reflect.Manifest
import java.math.BigDecimal


@Service
class KangaTransferShiftService(
    private val kangaAuthService: KangaAuthService,
    private val transferRepository: TransferRepository,
) {

    fun shift(
        type: OperationType,
        wallets: Pair<String, String>,
        currency: String,
        amount: BigDecimal,
        title: String? = null
    ): Mono<TransferResponse> =
        saveTransfer(Mono.create { sink ->
            Integration.restExecutor()
                .send(
                    WalletShiftRequest(kangaAuthService.apiKey, wallets, amount, currency, Option.apply(title)),
                    MonoSinkRestResponse(
                        sink,
                        Transfer(
                            ObjectId().toHexString(),
                            type,
                            TransferKind.SHIFT,
                            amount,
                            currency,
                            wallets.from(),
                            wallets.to(),
                            title
                        )
                    ),
                    Manifest { SuccessResponse::class.java }
                )
        }).map { TransferResponse(it) }

    private fun saveTransfer(mono: Mono<Transfer>): Mono<Transfer> =
        mono.flatMap {
            transferRepository.save(it)
        }

    private class MonoSinkRestResponse(private val sink: MonoSink<Transfer>, private val transfer: Transfer) :
        RestResponseCallback<SuccessResponse> {
        override fun onSuccess(response: SuccessResponse) {
            sink.success(transfer.apply { status = TransferStatus.DONE })
        }

        override fun onError(response: FailureRestResponse) {
            sink.success(
                transfer.apply {
                    status = TransferStatus.FAILURE
                    code = response.code()
                }
            )
        }
    }

}
