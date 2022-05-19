package exchange.kanga.domain.bon.scheduler

import exchange.kanga.domain.bon.cache.PartnerCache
import exchange.kanga.domain.bon.listener.VoucherUpdateEvent
import exchange.kanga.domain.bon.model.State
import exchange.kanga.domain.bon.model.Voucher
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.utils.common.Logger
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant

@Service
private class VoucherExpireScheduler(
    private val appContext: ApplicationContext,
    private val repository: VoucherRepository,
    private val partnerCache: PartnerCache,
) {

    private companion object : Logger

    @Scheduled(cron = "0 * 1 */1 * ?")
    private fun execute() {
        expireSignedBonAfterExpireTime()
    }

    private fun getPartner(partnerId: String) = partnerCache.getPartner(partnerId)

    private fun expireSignedBonAfterExpireTime() {
        val now = Instant.now()

        repository.findAllByStateAndExpireBefore(State.SIGNED, now)
            .doOnNext { logExpire(it) }
            .flatMap {
                repository.save(it.changeState(State.EXPIRED))
                    .doOnNext { voucher ->
                        val partner = getPartner(voucher.partnerId)
                            ?: kotlin.run {
                                error("SCHEDULER: voucher expire - cannot find partner for voucher: $voucher")
                                return@doOnNext
                            }
                        appContext.publishEvent(VoucherUpdateEvent(partner, voucher))
                    }
            }
            .subscribe()
    }

    private fun logExpire(voucher: Voucher) {
        info("Expire Voucher: id: ${voucher.id} - partner: ${voucher.partnerId} - status: ${voucher.state} - history: ${voucher.history}")
    }
}