package exchange.kanga.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.isZero() = compareTo(BigDecimal.ZERO) == 0
fun BigDecimal.isNotZero() = compareTo(BigDecimal.ZERO) != 0
fun BigDecimal.isOne() = compareTo(BigDecimal.ONE) == 0
fun BigDecimal.reverse(scale: Int = 8) = BigDecimal.ONE.divide(this, scale, RoundingMode.HALF_UP)
