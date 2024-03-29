package exchange.kanga.configuration

import exchange.kanga.authentications.KangaSecurityContextRepository
import exchange.kanga.authentications.UserAuthManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val authenticationManager: UserAuthManager,
    private val securityContextRepository: KangaSecurityContextRepository
) {

    private val frontendCorsConfiguration = CorsConfiguration().applyPermitDefaultValues()
    private val backOfficeCorsConfiguration = CorsConfiguration().applyPermitDefaultValues()

    private val corsConfiguration: Map<String, CorsConfiguration> = mapOf(
        "/api-admin/**" to backOfficeCorsConfiguration,
        "/api/**" to frontendCorsConfiguration
    )

    init {
        frontendCorsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "HEAD", "DELETE")
        backOfficeCorsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "HEAD", "DELETE")
    }

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http.cors().and()
            .exceptionHandling()
            .authenticationEntryPoint { serverWebExchange, _ ->
                Mono.fromRunnable {
                    serverWebExchange.response.statusCode = HttpStatus.UNAUTHORIZED
                }
            }
            .accessDeniedHandler { serverWebExchange, _ ->
                Mono.fromRunnable {
                    serverWebExchange.response.statusCode = HttpStatus.FORBIDDEN
                }
            }
            .and()
            .csrf().disable()
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .anyExchange().permitAll().and()
            .build()

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()

        corsConfiguration.entries.forEach { entry ->
            source.registerCorsConfiguration(entry.key, entry.value)
        }

        return source
    }

}
