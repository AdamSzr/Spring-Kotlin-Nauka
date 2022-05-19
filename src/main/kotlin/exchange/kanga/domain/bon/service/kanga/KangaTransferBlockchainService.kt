package exchange.kanga.domain.bon.service.kanga

import exchange.kanga.domain.bon.model.*
import exchange.kanga.domain.bon.repo.TransferRepository
import exchange.kanga.domain.bon.response.SendToBlockchainAddressWrongFormatFailureResponse
import exchange.kanga.otc.integration.Integration
import exchange.kanga.otc.integration.requests.kanga.WalletWithdrawRequest
import exchange.kanga.otc.integration.responses.FailureRestResponse
import exchange.kanga.otc.integration.responses.RestResponseCallback
import exchange.kanga.otc.integration.responses.SuccessResponse
import exchange.kanga.otc.integration.utils.Pair
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.isValidBlockchainAddress
import exchange.kanga.utils.toBlockChain
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import scala.reflect.Manifest
import java.math.BigDecimal


@Service
class KangaTransferBlockchainService(
    private val kangaAuthService: KangaAuthService,
    private val transferRepository: TransferRepository,
) {


    fun test() {
        blockChain(
            OperationType.BLOCKCHAIN_WITHDRAW,
            "".toBlockChain("..."),
            "oPLN",
            BigDecimal.TEN,
        )
    }

    fun blockChainWithAddressValidation(
        type: OperationType,
        wallets: Pair<String, String>,
        currency: String,
        amount: BigDecimal,
        title: String? = null,
    ): Mono<Response> =
        Mono.just("")
            .filter { validateBlockchainAddress(wallets.to()) }
            .flatMap<Response> { blockChain(type, wallets, currency, amount, title) }
            .switchIfEmpty { (SendToBlockchainAddressWrongFormatFailureResponse() as Response).toMono() }


    fun blockChain(
        type: OperationType,
        wallets: Pair<String, String>,
        currency: String,
        amount: BigDecimal,
        title: String? = null,
    ): Mono<TransferResponse> =
        saveTransfer(
            Mono.create { sink ->
                Integration.restExecutor()
                    .send(
                        WalletWithdrawRequest(
                            kangaAuthService.apiKey,
                            wallets.from(),
                            wallets.to(),
                            amount,
                            currency
                        ),
                        MonoSinkRestResponse(
                            sink,
                            Transfer(
                                ObjectId().toHexString(),
                                type,
                                TransferKind.BLOCKCHAIN,
                                amount,
                                currency,
                                wallets.from(),
                                wallets.to(),
                                title,
                            )
                        ),
                        Manifest { SuccessResponse::class.java }
                    )
            }
        )
            .map { TransferResponse(it) }

    private fun validateBlockchainAddress(address: String): Boolean =
        address.isValidBlockchainAddress()

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

