package exchange.kanga.domain.bon.scheduler

import exchange.kanga.domain.bon.model.State
import exchange.kanga.domain.bon.model.Voucher
import exchange.kanga.domain.bon.repo.VoucherRepository
import exchange.kanga.utils.common.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class VoucherCleanerScheduler(
    private val repository: VoucherRepository,
) {

    private companion object : Logger

    private val cleanList = listOf(State.UNSIGNED, State.EXPIRED, State.CANCELED)
    private val cleanNotUsedAfterDays = 7L
    private val cleanNotSignedAfterDays = 3L

    @Scheduled(cron = "0 * */1 * * ?")
    fun execute() {
        cleanCreatedAndNotSigned()
        cleanNotUsed()
    }

    private fun cleanCreatedAndNotSigned() {
        val cleanBefore = Instant.now().minus(cleanNotSignedAfterDays, ChronoUnit.DAYS)

        repository.findAllByState(State.CREATED)
            .filter { it.history.lastOrNull()?.time?.isBefore(cleanBefore) ?: false }
            .doOnNext { logClean(it) }
            .flatMap { repository.delete(it) }
            .subscribe()
    }

    private fun cleanNotUsed() {
        val cleanBefore = Instant.now().minus(cleanNotUsedAfterDays, ChronoUnit.DAYS)

        repository.findAllByStateIn(cleanList)
            .filter { it.history.lastOrNull()?.time?.isBefore(cleanBefore) ?: false }
            .doOnNext { logClean(it) }
            .flatMap { repository.delete(it) }
            .subscribe()
    }

    private fun logClean(voucher: Voucher) {
        info("Clean Voucher: id: ${voucher.id} - partner: ${voucher.partnerId} - status: ${voucher.state} - history: ${voucher.history}")
    }

}