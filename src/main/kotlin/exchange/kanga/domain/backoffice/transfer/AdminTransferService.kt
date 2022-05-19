package exchange.kanga.domain.backoffice.transfer

import exchange.kanga.domain.bon.repo.TransferRepository
import org.springframework.stereotype.Service

@Service
class AdminTransferService(
    private val transferRepository: TransferRepository,
) {

    fun getAll() = transferRepository.findAll()
        .collectList()
        .map { AdminTransferListResponse(it) }
}