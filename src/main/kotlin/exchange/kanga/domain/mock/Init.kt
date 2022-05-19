package exchange.kanga.domain.mock

import exchange.kanga.domain.bon.response.WalletAddressCreateFailureResponse
import exchange.kanga.client.KangaIntegrator
import exchange.kanga.domain.backoffice.login.LoginController
import exchange.kanga.domain.backoffice.reqres.UserSignUpRequest
import exchange.kanga.domain.bon.model.OperationType
import exchange.kanga.domain.bon.model.StableCoin
import exchange.kanga.domain.bon.service.RatesService
import exchange.kanga.domain.bon.service.kanga.KangaMarketPriceService
import exchange.kanga.domain.bon.service.kanga.KangaTransferShiftService
import exchange.kanga.utils.toWallet
import org.springframework.stereotype.Service
import java.math.BigDecimal
import javax.annotation.PostConstruct

@Service
class Init(
    private val kangaIntegrator: KangaIntegrator,
    private val ratesService: RatesService,
    private val loginController: LoginController,
    private val kangaTransferShiftService: KangaTransferShiftService,
    private val kangaMarketPriceService: KangaMarketPriceService,
) {


//    @PostConstruct
//    fun onInit() {
//        println(kangaMarketPriceService.getPrice(Currency.BTC, StableCoin.oPLN, BigDecimal("0.01")))
//        println(kangaMarketPriceService.getRate(StableCoin.BTC, StableCoin.oPLN, BigDecimal("0.1")))
//        println(kangaMarketPriceService.getRate(StableCoin.BTC, StableCoin.oPLN, BigDecimal.ONE))
//        println(kangaMarketPriceService.getRate(StableCoin.BTC, StableCoin.oPLN, BigDecimal("10")))
//        println(kangaMarketPriceService.getRate(StableCoin.BTC, StableCoin.oPLN, BigDecimal("15")))
//        println(kangaMarketPriceService.getRate(StableCoin.BTC, StableCoin.oPLN, BigDecimal("1000")))
//    }

    fun shiftTest() {
        kangaTransferShiftService.shift(
            OperationType.BLOCKCHAIN_WITHDRAW,
            "".toWallet("..."),
            "oPLN",
            BigDecimal.TEN,
            "Voucher: xxx"
        )
    }

////    @PostConstruct
//    fun onInit() {
//        println(ratesService.getRate(Currency.oEUR, StableCoin.oPLN))
//        println(ratesService.getRate(Currency.oPLN, StableCoin.oEUR))
//        println(ratesService.getRate(Currency.oPLN, StableCoin.oPLN))
//        println(ratesService.getRate(Currency.ETH, StableCoin.oPLN))
//        println(ratesService.getRate(Currency.ETH, StableCoin.oEUR))
//        println(ratesService.getRate(Currency.BTC, StableCoin.oPLN))
//        println(ratesService.getRate(Currency.BTC, StableCoin.oEUR))
//    }

//    @PostConstruct
//    fun onInit() {
//        val user = UserSignUpRequest("Tomek", "twronski@kanga.exchange", "Tomek")
//        val user2 = UserSignUpRequest("TestUser", "test@kanga.exchange", "TestUserPassword")
//        loginController.signUp(user).subscribe()
//        loginController.signUp(user2).subscribe()
//    }
    
//    @PostConstruct
//    fun onInit2() {
//        println("_ - _ - _ - _")
//        val address = kangaIntegrator.createWallet("testJarek003", Network.BITCOIN) ?: WalletAddressCreateFailureResponse()
//        println(address)
//        println("_ - _ - _ - _")

//
//        println(Duration.ofMinutes(1))
//        println(Duration.ofMinutes(1).toString())
//        println(Duration.ofDays(1))
//        println(Duration.ofDays(1).toString())
//
//
//
//        try {
//            println(Duration.parse("PT1M").toString())
//        } catch (e: Exception) { println(e.toString()) }
//        try {
//            println(Duration.parse("PT24H").toString())
//        } catch (e: Exception) { println(e.toString()) }
//        try {
//            println(Duration.parse("P1D").toString())
//        } catch (e: Exception) { println(e.toString()) }
//        try {
//            println(Duration.parse("P32D").toString())
//        } catch (e: Exception) { println(e.toString()) }

//val x = BuildInfoContributor
//        val v = getClass().getPackage().getImplementationVersion()

//    }
}