package exchange.kanga.domain.learn.listener

import exchange.kanga.CACHE_SVC_ROOT_DIR
import exchange.kanga.domain.learn.event.ServiceSignUpEvent
import exchange.kanga.providers.Drive
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class ServiceSignUpListener(private val drive: Drive) {

    @EventListener
    fun createDefaultDirectory(event: ServiceSignUpEvent) {
        drive.createDirectory(CACHE_SVC_ROOT_DIR.plus("/${event.serviceName}"))
    }
}