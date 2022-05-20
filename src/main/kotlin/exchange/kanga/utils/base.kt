package exchange.kanga.utils

import java.util.Arrays

object UtilTools {
    fun randomString(len:Int) = (1..len)
        .map { kotlin.random.Random.nextInt(0, 25)+65 }
        .map{ i -> Char(i) }
        .joinToString("");

}

