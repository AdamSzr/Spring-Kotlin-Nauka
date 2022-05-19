package exchange.kanga.utils.common

import io.sentry.Sentry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.DependsOn

@DependsOn("SentryConfiguration")
interface Logger {

    companion object {
        const val UNKNOWN_ERROR: String = "unknown error"
    }

    fun logger() = this

    fun info(string: String?) { LoggerFactory.getLogger(this.javaClass).info(string) }

    fun error(string: String?, sendError: Boolean = true) {
        LoggerFactory.getLogger(this.javaClass).error(string)

        if (sendError) try {
            Sentry.captureMessage(string ?: UNKNOWN_ERROR)
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error(e.toString())
        }
    }
    fun error(string: String?, throwable: Throwable) {
        val message = "Message: $string - Throwable: ${throwable.message}"
        try {
            Sentry.captureMessage(message)
            Sentry.captureException(throwable, string)
        } catch (e: Exception) {
            LoggerFactory.getLogger(this.javaClass).error(string)
        }
        LoggerFactory.getLogger(this.javaClass).error(message)

    }

    fun logRequest(endpoint: String, host: String?, header: Map<String, String>?, body: Any? = null) {
        info("Request: $endpoint - Host: $host - Header: $header - Body: $body")
    }
//    fun error(any: Any) {
//        Sentry.captureMessage(any.toString())
//        LoggerFactory.getLogger(this.javaClass).error(any.toString())
//    }

    private fun <T> logger(clazz: Class<T>): Logger =  LoggerFactory.getLogger(clazz)
}

/**
 * PROBLEM
 * Spring Autowiring order
 * @see: https://stackoverflow.com/questions/46393645/execution-order-of-postconstruct
 */