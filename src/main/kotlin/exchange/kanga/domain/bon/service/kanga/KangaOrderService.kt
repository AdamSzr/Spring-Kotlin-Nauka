package exchange.kanga.domain.bon.service.kanga

import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.otc.integration.Integration
import exchange.kanga.otc.integration.requests.kanga.ASK
import exchange.kanga.otc.integration.requests.kanga.CreateOrderRequest
import exchange.kanga.otc.integration.responses.FailureRestResponse
import exchange.kanga.otc.integration.responses.RestResponseCallback
import exchange.kanga.otc.integration.responses.kanga.CreateOrderResponse
import exchange.kanga.otc.integration.utils.Pair
import exchange.kanga.utils.common.Logger
import org.springframework.stereotype.Service
import scala.Option
import scala.reflect.Manifest
import java.math.BigDecimal

@Service
class KangaOrderService {

    private companion object : Logger

    /**
     * ETH -> oPLN // 1 ETH -> 8.000 oPLN // SELL 1 ETH for 0.01 oPLN
     *
     */
    fun createOrder(fromCurrency: String, toStableCoin: StableCoin, amount: BigDecimal, walletKey: String) = create(
        Pair(fromCurrency, toStableCoin.name),
        amount,
        BigDecimal("0.01"),
        Option.apply(walletKey),
    )

    fun create(
        pair: Pair<String, String>,
        quantity: BigDecimal,
        price: BigDecimal,
        walletKey: Option<String>,
    ) {
        Integration.restExecutor().send(
            CreateOrderRequest(ASK(), pair, quantity, price, walletKey),
            object : RestResponseCallback<CreateOrderResponse> {
                override fun onSuccess(response: CreateOrderResponse) {
                    info("Kanga Order Create (SUCCESS): id: ${response.orderId()} - pair: $pair - quantity: $quantity - price: $price - walletKey: $walletKey  ")
                }

                override fun onError(response: FailureRestResponse) {
                    logger().error(response.message())
                }
            },
            Manifest { CreateOrderResponse::class.java }
        )
    }
}