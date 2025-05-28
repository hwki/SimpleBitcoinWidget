package com.brentpanther.bitcoinwidget

import android.os.Build
import com.brentpanther.bitcoinwidget.R.drawable.*
import com.brentpanther.bitcoinwidget.Theme.SOLID
import com.brentpanther.bitcoinwidget.Theme.TRANSPARENT
import java.util.SortedSet

enum class Coin(val coinName: String, val coinGeckoId: String, private vararg val themes: IconTheme) {

    CUSTOM("Custom", "", IconTheme(SOLID, ic_placeholder)),
    ONE_INCH("1inch", "1inch", IconTheme(SOLID, ic_1inch)),
    AAVE("Aave", "aave", IconTheme(SOLID, ic_aave)),
    ADA("Cardano", "cardano", IconTheme(SOLID, ic_ada)),
    ALGO("Algorand", "algorand", IconTheme(SOLID, ic_algo, ic_algo_white)),
    APE("ApeCoin", "apecoin", IconTheme(SOLID, ic_ape)),
    APT("Aptos", "aptos", IconTheme(SOLID, ic_apt)),
    ARB("Arbitrum", "arbitrum", IconTheme(SOLID, ic_arb)),
    ARRR("Pirate Chain", "pirate-chain", IconTheme(SOLID, ic_arrr)),
    ASTR("Astar", "astar", IconTheme(SOLID, ic_astr)),
    ATOM("Cosmos", "cosmos", IconTheme(SOLID, ic_atom)),
    AVA("Travala.com", "concierge-io", IconTheme(SOLID, ic_ava)),
    AVAX("Avalanche","avalanche-2",  IconTheme(SOLID, ic_avax, ic_avax_dark)),
    AXS("Axie Infinity", "axie-infinity", IconTheme(SOLID, ic_axs)),
    BAL("Balancer", "balancer", IconTheme(SOLID, ic_bal)),
    BAND("Band Protocol", "band-protocol", IconTheme(SOLID, ic_band_color)),
    BAT("Basic Attention Token", "basic-attention-token", IconTheme(SOLID, ic_bat)),
    BCD("Bitcoin Diamond", "bitcoin-diamond", IconTheme(SOLID, ic_bcd, ic_bcd_white)),
    BCH("Bitcoin Cash", "bitcoin-cash", IconTheme(SOLID, ic_bch, ic_bch_dark)) {
        override fun getUnits() =
            listOf(CoinUnit("BCH", 1.0), CoinUnit("mBCH", .001), CoinUnit("μBCH", .000001))
    },
    BNB("Binance Coin", "binancecoin", IconTheme(SOLID, ic_bnb)),
    BEST("Bitpanda Ecosystem Token", "bitpanda-ecosystem-token", IconTheme(SOLID, ic_best)),
    BNT("Bancor Network Token", "bancor", IconTheme(SOLID, ic_bnt)),
    BONK("Bonk", "bonk", IconTheme(SOLID, ic_bonk)),
    BSV("Bitcoin SV", "bitcoin-cash-sv", IconTheme(SOLID, ic_bsv, ic_bsv_dark)),
    BTC("Bitcoin", "bitcoin", IconTheme(SOLID, ic_btc, ic_btc_dark)) {
        override fun getUnits() =
            listOf(CoinUnit("BTC", 1.0), CoinUnit("mBTC", .001), CoinUnit("Bit", .000001), CoinUnit("Sat", .00000001))
    },
    BTG("Bitcoin Gold", "bitcoin-gold", IconTheme(SOLID, ic_btg, ic_btg_dark)),
    BTM("Bytom", "bytom", IconTheme(SOLID, ic_btm, ic_btm_gray)),
    BTT("BitTorrent", "bittorrent", IconTheme(SOLID, ic_btt)),
    CEL("Celsius", "celsius-degree-token", IconTheme(SOLID, ic_cel)),
    CHZ("Chiliz", "chiliz",IconTheme(SOLID, ic_chz)),
    COMP("Compound", "compound-coin", IconTheme(SOLID, ic_comp_black, ic_comp_white)),
    CORE("Core", "coredaoorg", IconTheme(SOLID, ic_core)),
    CRO("Crypto.com Coin", "crypto-com-chain", IconTheme(SOLID, ic_cro, ic_cro_white)),
    CRV("Curve DAO Token", "curve-dao-token", IconTheme(SOLID, ic_crv)),
    CUBE("Somnium Space CUBEs", "somnium-space-cubes", IconTheme(SOLID, ic_cube_black, ic_cube_white)),
    DAI("Dai", "dai", IconTheme(SOLID, ic_dai_color)),
    DASH("Dash", "dash", IconTheme(SOLID, ic_dash, ic_dash_dark)),
    DCR("Decred", "decred", IconTheme(SOLID, ic_dcr)),
    DOGE("Dogecoin", "dogecoin", IconTheme(SOLID, ic_doge)),
    DOT("Polkadot", "polkadot", IconTheme(SOLID, ic_dot)),
    DYDX("dYdX", "dydx-chain", IconTheme(SOLID, ic_dydx)),
    EGLD("Elrond", "elrond-erd-2", IconTheme(SOLID, ic_egld_dark, ic_egld_white)),
    ENJ("Enjin Coin", "enjincoin", IconTheme(SOLID, ic_enj)),
    ENS("Ethereum Name Service", "ethereum-name-service", IconTheme(SOLID, ic_ens)),
    EOS("EOS", "eos", IconTheme(SOLID, ic_eos_black, ic_eos_white), IconTheme(TRANSPARENT, ic_eos_white)),
    ETC("Ethereum Classic", "ethereum-classic", IconTheme(SOLID, ic_etc)),
    ETH("Ethereum", "ethereum", IconTheme(SOLID, ic_eth)),
    FET("Fetch.ai", "fetch-ai", IconTheme(SOLID, ic_fet)),
    FIL("Filecoin", "filecoin", IconTheme(SOLID, ic_fil)),
    FIRO("Firo", "zcoin", IconTheme(SOLID, ic_firo, ic_firo_dark)),
    FLR("Flare", "flare-networks", IconTheme(SOLID, ic_flr)),
    FTT("FTX Token", "ftx-token", IconTheme(SOLID, ic_ftt)),
    GALA("Gala", "gala", IconTheme(SOLID, ic_gala, ic_gala_white)),
    GNO("Gnosis", "gnosis", IconTheme(SOLID, ic_gno_color)),
    GNT("Golem", "golem", IconTheme(SOLID, ic_gnt_blue)),
    GRIN("Grin", "grin", IconTheme(SOLID, ic_grin_color_black)),
    GRT("The Graph", "the-graph", IconTheme(SOLID, ic_grt)),
    HBAR("Hedera Hashgraph", "hedera-hashgraph", IconTheme(SOLID, ic_hbar, ic_hbar_white)),
    HNS("Handshake", "handshake", IconTheme(SOLID, ic_hns, ic_hns_dark)),
    HT("Huobi Token", "huobi-token", IconTheme(SOLID, ic_ht)),
    ICX("Icon", "icon", IconTheme(SOLID, ic_icx)),
    ID("Space ID", "space-id", IconTheme(SOLID, ic_id)),
    IMX("Immutable", "immutable-x", IconTheme(SOLID, ic_imx)),
    INJ("Injective", "injective-protocol", IconTheme(SOLID, ic_inj)),
    IOTA("Iota", "iota", IconTheme(SOLID, ic_iota, ic_iota_white)),
    KAS("Kaspa", "kaspa", IconTheme(SOLID, ic_kas_color)),
    KAVA("Kava", "kava", IconTheme(SOLID, ic_kava)),
    KMD("Komodo", "komodo", IconTheme(SOLID, ic_kmd)),
    KNC("Kyber Network", "kyber-network", IconTheme(SOLID, ic_knc_color)),
    KSM("Kusama", "kusama", IconTheme(SOLID, ic_ksm_black, ic_ksm_white)),
    LDO("Lido DAO", "lido-dao", IconTheme(SOLID, ic_ldo)),
    LEO("LEO Token", "leo-token", IconTheme(SOLID, ic_leo)),
    LINK("ChainLink", "chainlink", IconTheme(SOLID, ic_link)),
    LRC("Loopring", "loopring", IconTheme(SOLID, ic_lrc)),
    LSK("Lisk", "lisk", IconTheme(SOLID, ic_lsk)),
    LTC("Litecoin", "litecoin", IconTheme(SOLID, ic_ltc)) {
        override fun getUnits() =
            listOf(CoinUnit("LTC", 1.0), CoinUnit("mŁ", .001), CoinUnit("μŁ", .0000001))
    },
    LTO("LTO Network", "lto-network", IconTheme(SOLID, ic_lto)),
    LUNA("Terra", "terra-luna-2", IconTheme(SOLID, ic_luna)),
    LUNC("Terra Luna Classic", "terra-luna", IconTheme(SOLID, ic_lunc)),
    MANA("Decentraland", "decentraland", IconTheme(SOLID, ic_mana)),
    MATIC("Polygon", "matic-network", IconTheme(SOLID, ic_matic)),
    MCO("MCO", "monaco", IconTheme(SOLID, ic_mco, ic_mco_white)),
    MKR("Maker", "maker", IconTheme(SOLID, ic_mkr)),
    MLN("Melon", "melon", IconTheme(SOLID, ic_mln)),
    NANO("Nano", "nano", IconTheme(SOLID, ic_nano)),
    NEAR("Near", "near", IconTheme(SOLID, ic_near)),
    NEO("NEO", "neo", IconTheme(SOLID, ic_neo)),
    NEXO("NEXO", "nexo", IconTheme(SOLID, ic_nexo)),
    NRG("Energi", "energi", IconTheme(SOLID, ic_nrg)),
    OKB("OKB", "okb", IconTheme(SOLID, ic_okb_black, ic_okb_white)),
    OMG("OMG", "omisego", IconTheme(SOLID, ic_omg)),
    ONT("Ontology", "ontology", IconTheme(SOLID, ic_ont)),
    OP("Optimism", "optimism", IconTheme(SOLID, ic_op)),
    PAN("Pantos", "pantos", IconTheme(SOLID, ic_pan)),
    PAX("Paxos Standard", "paxos-standard", IconTheme(SOLID, ic_pax)),
    PAXG("PAX Gold", "pax-gold", IconTheme(SOLID, ic_paxg_color)),
    PEPE("Pepe", "pepe", IconTheme(SOLID, ic_pepe)),
    PI("Pi", "picoin", IconTheme(SOLID, ic_pi)),
    POWR("Power Ledger", "power-ledger", IconTheme(SOLID, ic_powr_color)),
    PPC("Peercoin", "peercoin", IconTheme(SOLID, ic_ppc)),
    QNT("Quant", "quant-network", IconTheme(SOLID, ic_qnt)),
    QTUM("Qtum", "qtum", IconTheme(SOLID, ic_qtum)),
    RDD("Reddcoin", "reddcoin", IconTheme(SOLID, ic_rdd)),
    REN("REN", "republic-protocol", IconTheme(SOLID, ic_ren)),
    REP("Augur", "augur", IconTheme(SOLID, ic_rep)),
    RENDER("Render", "render-token", IconTheme(SOLID, ic_rndr)),
    RPL("Rocket Pool", "rocket-pool", IconTheme(SOLID, ic_rpl)),
    RUNE("THORChain", "thorchain", IconTheme(SOLID, ic_rune)),
    RVN("Ravencoin", "ravencoin", IconTheme(SOLID, ic_rvn)),
    SAND("The Sandbox", "the-sandbox", IconTheme(SOLID, ic_sand)),
    SHIB("Shiba Inu", "shiba-inu", IconTheme(SOLID, ic_shib)),
    SNX("Synthetix Network Token", "havven", IconTheme(SOLID, ic_snx)),
    SOL("Solana", "solana", IconTheme(SOLID, ic_sol)),
    SOLO("Sologenic", "solo-coin", IconTheme(SOLID, ic_solo)),
    S("Sonic", "sonic-3", IconTheme(SOLID, ic_s)),
    SPACE("MicrovisionChain", "microvisionchain", IconTheme(SOLID, ic_space)),
    STG("Stargate Finance", "stargate-finance", IconTheme(SOLID, ic_stg)),
    STORJ("Storj", "storj", IconTheme(SOLID, ic_storj)),
    SUI("Sui", "sui", IconTheme(SOLID, ic_sui)),
    SUSHI("Sushi", "sushi", IconTheme(SOLID, ic_sushi)),
    THETA("Theta Network", "theta-token", IconTheme(SOLID, ic_theta)),
    TRX("Tron", "tron", IconTheme(SOLID, ic_trx)),
    UMA("UMA", "uma", IconTheme(SOLID, ic_uma)),
    UNI("Uniswap", "uniswap", IconTheme(SOLID, ic_uni)),
    VET("VeChain", "vechain", IconTheme(SOLID, ic_vet)),
    VRSC("Verus Coin", "verus-coin", IconTheme(SOLID, ic_vrsc)),
    VTC("Vertcoin", "vertcoin", IconTheme(SOLID, ic_vtc)),
    WAVES("Waves", "waves", IconTheme(SOLID, ic_waves)),
    WBTC("Wrapped Bitcoin", "wrapped-bitcoin", IconTheme(SOLID, ic_wbtc)),
    XAUT("Tether Gold", "tether-gold", IconTheme(SOLID, ic_xaut_color)),
    XEM("NEM", "nem", IconTheme(SOLID, ic_xem, ic_xem_dark_gray)),
    XLM("Stellar", "stellar", IconTheme(SOLID, ic_xlm, ic_xlm_white)),
    XMR("Monero", "monero", IconTheme(SOLID, ic_xmr), IconTheme(TRANSPARENT, ic_xmr, dark = ic_xmr_dark)),
    XRP("Ripple", "ripple", IconTheme(SOLID, ic_xrp_black, ic_xrp_white), IconTheme(TRANSPARENT, ic_xrp_white)),
    XTZ("Tezos", "tezos", IconTheme(SOLID, ic_xtz)),
    XVG("Verge", "verge", IconTheme(SOLID, ic_xvg)),
    XYM("Symbol", "symbol", IconTheme(SOLID, ic_xym)),
    YFI("yearn.finance", "yearn-finance", IconTheme(SOLID, ic_yfi)),
    ZEC("Zcash", "zcash", IconTheme(SOLID, ic_zec, ic_zec_dark), IconTheme(TRANSPARENT, ic_zec)),
    ZIL("Zilliqa", "zilliqa", IconTheme(SOLID, ic_zil)),
    ZRX("0x", "0x", IconTheme(SOLID, ic_zrx_black, ic_zrx_white));

    open fun getUnits() = emptyList<CoinUnit>()

    fun getUnitAmount(text: String) = getUnits().firstOrNull { it.text == text }?.amount ?: 1.0

    fun getIcon(theme: Theme, dark: Boolean): Int {
        val iconTheme = themes.firstOrNull { it.theme == theme } ?: themes.first()
        return if (dark) iconTheme.dark else iconTheme.light
    }

    fun getSymbol() = if (this == ONE_INCH) "1INCH" else name

    companion object {

        var COIN_NAMES: SortedSet<String> = entries.map { it.name }.toSortedSet(String.CASE_INSENSITIVE_ORDER)

        fun getByName(name: String): Coin? {
            if (name == "1INCH") return ONE_INCH
            return Coin.runCatching { valueOf(name) }.getOrNull()
        }

        fun getVirtualCurrencyFormat(currency: String, hideSymbol: Boolean): String {
            if (hideSymbol) {
                return "#,###"
            }
            return when (currency) {
                "BTC" ->
                    // bitcoin symbol added in Oreo
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "₿ #,###" else "Ƀ #,###"
                "LTC" -> "Ł #,###"
                "DOGE" -> "Ð #,###"
                else -> "#,### $currency"
            }
        }
    }

}

class CoinUnit(val text: String, val amount: Double)
