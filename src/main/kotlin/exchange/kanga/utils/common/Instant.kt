package exchange.kanga.utils.common

import java.time.Instant
import java.time.temporal.ChronoUnit

fun Instant.roundToHourFloor() = this
    .truncatedTo(ChronoUnit.HOURS)
    .plus(1L, ChronoUnit.HOURS)