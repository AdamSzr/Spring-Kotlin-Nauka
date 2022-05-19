package exchange.kanga.domain.bon.service.voucher

import exchange.kanga.client.KangaIntegrator
import exchange.kanga.domain.bon.cache.PartnerCache
import exchange.kanga.domain.bon.listener.VoucherCreateEvent
import exchange.kanga.domain.bon.model.*
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.domain.bon.reqres.VoucherCreateOffChainResponse
import exchange.kanga.domain.bon.reqres.VoucherCreateRequest
import exchange.kanga.domain.bon.reqres.VoucherCreateResponse
import exchange.kanga.domain.bon.response.BadRequest
import exchange.kanga.domain.bon.response.MinimalDepositFailure
import exchange.kanga.domain.bon.response.PartnerNotExist
import exchange.kanga.domain.bon.response.WalletAddressCreateFailureResponse
import exchange.kanga.domain.bon.service.MinimumDepositValidator
import exchange.kanga.domain.bon.utils.StringGeneratorUtils
import exchange.kanga.utils.common.Response
import exchange.kanga.utils.common.roundToHourFloor
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class VoucherCreateService(
    private val appContext: ApplicationContext,
    private val partnerCache: PartnerCache,
    private val repo: VoucherRepository,

    private val kangaIntegrator: KangaIntegrator,

    private val minimumDepositValidator: MinimumDepositValidator,

    ) {

    private fun getPartner(id: String) = partnerCache.getPartner(id)

    fun create(body: VoucherCreateRequest): Response {

        if (catchBadRequestVoucherCreate(body)) return BadRequest()

        val partner = getPartner(body.partner) ?: return PartnerNotExist()

        // get billing mode from request or by default from partner
        val billingMode = body.billingMode ?: partner.billingMode

        // validate: if ON-CHAIN amount is less than minimal deposit for specific network and currency
//        if (billingMode == BillingMode.ON_CHAIN) {
//            val minimalDeposit = checkRequiredAmountOfMinimumDeposit(body.currency, body.network!!, body.amount)
//            if (minimalDeposit != null) return MinimalDepositFailure(minimalDeposit)
//        }

        // create voucher depends on billing mode and type
        val voucher = when {
            (billingMode == BillingMode.ON_CHAIN && body.type == VoucherType.WITHDRAW) ->
                createOnChain(body)
                    ?: return WalletAddressCreateFailureResponse()
            else ->
                createOffChain(body)
        }

        // set expire date
        if (partner.voucherExpireTime != null)
            voucher.setExpireDate(Instant.now().plus(partner.voucherExpireTime).roundToHourFloor())

        // save to db
        repo.save(voucher).subscribe()

        // notify to sign
        appContext.publishEvent(VoucherCreateEvent(partner, voucher))

        // return response
        return when {
            (billingMode == BillingMode.ON_CHAIN && body.type == VoucherType.WITHDRAW) -> VoucherCreateResponse(
                voucher.id,
                voucher.address,
                voucher.network,
                voucher.expire,
            )
            else -> VoucherCreateOffChainResponse(voucher.id, voucher.expire)
        }
    }

    private fun createOffChain(body: VoucherCreateRequest): Voucher {
        return Voucher(
            pin = StringGeneratorUtils.getRandomDigitsString(),
            partnerId = body.partner,
            type = body.type,
            currency = body.currency,
            operator = body.operator,
            history = mutableListOf(VoucherHistory()),
            email = body.email,
            kyc = body.kyc,
            control = body.control,
            id = StringGeneratorUtils.getRandomString(),
        )
    }

    private fun createOnChain(body: VoucherCreateRequest): Voucher? {
        val id = StringGeneratorUtils.getRandomString()
        val now = Instant.now()

        val address = generateAddress(id, body.network!!, now) ?: return null

        return Voucher(
            pin = StringGeneratorUtils.getRandomDigitsString(),
            partnerId = body.partner,
            type = body.type,
            currency = body.currency,
            network = body.network,
            address = address,
            operator = body.operator,
            history = mutableListOf(VoucherHistory(time = now)),
            email = body.email,
            kyc = body.kyc,
            control = body.control,
            id = id
        )
    }


    private fun generateAddress(id: String, network: String, created: Instant): String? {
        val walletName = generateWalletName(id, created)
        return kangaIntegrator.createWallet(walletName, network)
    }

    private fun generateWalletName(id: String, created: Instant) =
        "Voucher:$created:$id"




    /**
     * Validators etc.
     */
    private fun catchBadRequestVoucherCreate(body: VoucherCreateRequest) =
        body.partner.isBlank()
                || body.control.isBlank()
                || body.control.length > 64
                || (body.billingMode == BillingMode.ON_CHAIN && body.network == null)


    private fun checkRequiredAmountOfMinimumDeposit(currency: String, network: String, amount: BigDecimal) =
        minimumDepositValidator.getRequiredIfNotHaveMinimumAmount(currency, network, amount)

}