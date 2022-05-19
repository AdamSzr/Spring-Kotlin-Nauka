package exchange.kanga.domain.bon.utils

interface StringGeneratorUtils {
    companion object {

        private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        private val allowedDigits: CharRange = ('0'..'9')

        fun getRandomString(length: Int = 8) : String {
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }

        fun getRandomDigitsString(length: Int = 6) : String {
            return (1..length)
                .map { allowedDigits.random() }
                .joinToString("")
        }
    }
}