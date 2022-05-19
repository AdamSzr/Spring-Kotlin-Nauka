package exchange.kanga.utils

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

interface InstantParser {
    companion object {

        private val format: DateTimeFormatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
            .toFormatter()

        fun parse(string: String?) = try {
            LocalDateTime
                .parse(string, format)
                .toInstant(ZoneOffset.UTC)
        } catch (e: Exception) {
            null
        }
    }
}