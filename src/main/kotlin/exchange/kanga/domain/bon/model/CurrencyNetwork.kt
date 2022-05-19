package exchange.kanga.domain.bon.model

//enum class CurrencyNetwork(
//    val label: String? = null,
//    val explorerUrl: String? = null,
//    val numOfConfirmation: Int = 0,
//) {
//    BITCOIN("Bitcoin", "https://www.blockchain.com/btc/address/", 3),
//    ETHER(
//        "Ethereum (ERC-20)",
//        "https://etherscan.io/address/",
//        20
//    ),
//    BSC("BSC (BEP-20)", "https://bscscan.com/address/", 40),
//    BTCV("Bitcoin Vault", "", 0),
//    RSK("RSK", "", 0);
//}

data class CurrencyNetwork(
    val name: String,
    val label: String? = null,
    val explorerUrl: String? = null,
    val numOfConfirmation: Int = 0,
) {


}

