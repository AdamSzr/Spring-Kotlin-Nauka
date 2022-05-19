package exchange.kanga.configuration

import exchange.kanga.utils.common.Logger
import org.springframework.context.annotation.Configuration
import io.sentry.Sentry
import io.sentry.SpanStatus
import org.springframework.beans.factory.annotation.Value
import javax.annotation.PostConstruct

@Configuration
class SentryConfiguration {

    private companion object : Logger

    @Value("\${sentry.dns}")
    lateinit var dns: String

    @PostConstruct
    private fun onInit() {
        Sentry.init { options ->
            options.dsn = dns

            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.tracesSampleRate = 1.0

            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true)

        }

        logger().info("Sentry init - dns: $dns")
    }

    // https://sentry.io/onboarding/kanga-exchange-yt/get-started/
    fun transaction() {
        val transaction = Sentry.startTransaction("processOrderBatch()", "task")
        try {
//                    processOrderBatch()
        } catch (e: Exception) {
            transaction.throwable = e
            transaction.status = SpanStatus.INTERNAL_ERROR
            throw e
        } finally {
            transaction.finish();
        }
    }
}