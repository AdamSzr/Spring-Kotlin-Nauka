package exchange.kanga.client

import exchange.kanga.domain.bon.service.kanga.KangaAuthService
import exchange.kanga.otc.integration.Integration
import exchange.kanga.otc.integration.requests.kanga.CreateAddressRequest
import exchange.kanga.otc.integration.requests.kanga.CreateWalletRequest
import exchange.kanga.otc.integration.requests.kanga.GetWalletsRequest
import exchange.kanga.otc.integration.responses.FailureRestResponse
import exchange.kanga.otc.integration.responses.RestResponseCallback
import exchange.kanga.otc.integration.responses.SuccessResponse
import exchange.kanga.otc.integration.responses.kanga.CreateAddressResponse
import exchange.kanga.utils.common.Logger
import org.springframework.stereotype.Service
import scala.reflect.Manifest

@Service
class KangaIntegrator(
    private val kangaAuthService: KangaAuthService
) {

    private companion object : Logger

    fun createWallet(walletName: String, network: String): String? {
        var walletNameResponse: String? = null
        Integration.restExecutor()
            .send(
                GetWalletsRequest(walletName, kangaAuthService.apiKey),
                object : RestResponseCallback<SuccessResponse> {
                    override fun onSuccess(response: SuccessResponse) {
                        logger().info("Wallet OK: $walletName - Response: $response")
                        walletNameResponse = "WALLET ALREADY EXIST"
                    }
                    override fun onError(response: FailureRestResponse) {
                        logger().info("Wallet Error - Create new Wallet for: $walletName , Code: ${response.code()} , Response: $response")
                        Integration.restExecutor()
                            .send(
                                CreateWalletRequest(walletName),
                                RestResponseCallback<SuccessResponse> {
                                    val address: String? = createAddress(walletName, network)
                                    walletNameResponse = address
                                                                      },
                                Manifest { SuccessResponse::class.java }
                            )
                    }
                },
                Manifest { SuccessResponse::class.java }
            )
        return walletNameResponse
    }

    fun createAddress(walletName: String, currency: String): String? {
        var address: String? = null
        Integration.restExecutor()
            .send(
                CreateAddressRequest(kangaAuthService.apiKey, walletName, currency),
                object : RestResponseCallback<CreateAddressResponse> {
                    override fun onError(response: FailureRestResponse) {
                        error(response.toString())
                    }
                    override fun onSuccess(response: CreateAddressResponse) {
                        error(response.toString(), false)
                        address = response.address()
                    }
                },
                Manifest { CreateAddressResponse::class.java }
            )
        return address
    }
}



