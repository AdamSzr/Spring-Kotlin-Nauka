package exchange.kanga.domain.backoffice.voucher

import exchange.kanga.domain.bon.cache.VoucherCurrenciesCache
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.annotation.PostConstruct

@Controller
class AdminVoucherCurrenciesCacheController(
    private val service: AdminVoucherCurrenciesCacheService,
) {
}


@Service
//@DependsOn("voucherCurrenciesCache")
class AdminVoucherCurrenciesCacheService(
    private val voucherCurrenciesCache: VoucherCurrenciesCache,
) {

    private val filePath = voucherCurrenciesCache.path

    @PostConstruct
    private fun onInit() {
        println("sleep .... in")
        println("sleep .... out")

        reloadCacheFromUrl("https://otc.kanga.exchange/data/voucher-currencies.json")
    }

    fun reloadCacheFromUrl(url: String) {
        downloadFile(URL(url), filePath)
        voucherCurrenciesCache.refreshCacheFromJson()
    }

    private fun downloadFile(url: URL, fileName: String) {
        url.openStream().use { Files.copy(it, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING) }
    }

}
