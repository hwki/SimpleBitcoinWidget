package com.brentpanther.bitcoinwidget

import android.os.Build
import com.brentpanther.bitcoinwidget.R.drawable.ic_1inch
import com.brentpanther.bitcoinwidget.R.drawable.ic_aave
import com.brentpanther.bitcoinwidget.R.drawable.ic_ada
import com.brentpanther.bitcoinwidget.R.drawable.ic_algo
import com.brentpanther.bitcoinwidget.R.drawable.ic_algo_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_ape
import com.brentpanther.bitcoinwidget.R.drawable.ic_arrr
import com.brentpanther.bitcoinwidget.R.drawable.ic_atom
import com.brentpanther.bitcoinwidget.R.drawable.ic_ava
import com.brentpanther.bitcoinwidget.R.drawable.ic_avax
import com.brentpanther.bitcoinwidget.R.drawable.ic_avax_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_axs
import com.brentpanther.bitcoinwidget.R.drawable.ic_bal
import com.brentpanther.bitcoinwidget.R.drawable.ic_band_color
import com.brentpanther.bitcoinwidget.R.drawable.ic_bat
import com.brentpanther.bitcoinwidget.R.drawable.ic_bcd
import com.brentpanther.bitcoinwidget.R.drawable.ic_bcd_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_bch
import com.brentpanther.bitcoinwidget.R.drawable.ic_bch_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_best
import com.brentpanther.bitcoinwidget.R.drawable.ic_bnb
import com.brentpanther.bitcoinwidget.R.drawable.ic_bnt
import com.brentpanther.bitcoinwidget.R.drawable.ic_bsv
import com.brentpanther.bitcoinwidget.R.drawable.ic_bsv_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_btc
import com.brentpanther.bitcoinwidget.R.drawable.ic_btc_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_btg
import com.brentpanther.bitcoinwidget.R.drawable.ic_btg_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_btm
import com.brentpanther.bitcoinwidget.R.drawable.ic_btm_gray
import com.brentpanther.bitcoinwidget.R.drawable.ic_btt
import com.brentpanther.bitcoinwidget.R.drawable.ic_cel
import com.brentpanther.bitcoinwidget.R.drawable.ic_chz
import com.brentpanther.bitcoinwidget.R.drawable.ic_comp_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_comp_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_cro
import com.brentpanther.bitcoinwidget.R.drawable.ic_cro_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_crv
import com.brentpanther.bitcoinwidget.R.drawable.ic_cube_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_cube_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_dai_color
import com.brentpanther.bitcoinwidget.R.drawable.ic_dash
import com.brentpanther.bitcoinwidget.R.drawable.ic_dash_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_dcr
import com.brentpanther.bitcoinwidget.R.drawable.ic_doge
import com.brentpanther.bitcoinwidget.R.drawable.ic_dot_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_dot_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_egld_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_egld_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_enj
import com.brentpanther.bitcoinwidget.R.drawable.ic_eos_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_eos_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_etc
import com.brentpanther.bitcoinwidget.R.drawable.ic_eth
import com.brentpanther.bitcoinwidget.R.drawable.ic_fil
import com.brentpanther.bitcoinwidget.R.drawable.ic_firo
import com.brentpanther.bitcoinwidget.R.drawable.ic_firo_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_ftm
import com.brentpanther.bitcoinwidget.R.drawable.ic_ftt
import com.brentpanther.bitcoinwidget.R.drawable.ic_gala
import com.brentpanther.bitcoinwidget.R.drawable.ic_gala_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_gno_color
import com.brentpanther.bitcoinwidget.R.drawable.ic_gnt_blue
import com.brentpanther.bitcoinwidget.R.drawable.ic_grin_color_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_grt
import com.brentpanther.bitcoinwidget.R.drawable.ic_hbar
import com.brentpanther.bitcoinwidget.R.drawable.ic_hbar_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_hns
import com.brentpanther.bitcoinwidget.R.drawable.ic_hns_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_ht
import com.brentpanther.bitcoinwidget.R.drawable.ic_icx
import com.brentpanther.bitcoinwidget.R.drawable.ic_iota
import com.brentpanther.bitcoinwidget.R.drawable.ic_iota_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_kava
import com.brentpanther.bitcoinwidget.R.drawable.ic_kmd
import com.brentpanther.bitcoinwidget.R.drawable.ic_knc_color
import com.brentpanther.bitcoinwidget.R.drawable.ic_ksm_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_ksm_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_leo
import com.brentpanther.bitcoinwidget.R.drawable.ic_link
import com.brentpanther.bitcoinwidget.R.drawable.ic_lrc
import com.brentpanther.bitcoinwidget.R.drawable.ic_lsk
import com.brentpanther.bitcoinwidget.R.drawable.ic_ltc
import com.brentpanther.bitcoinwidget.R.drawable.ic_lto
import com.brentpanther.bitcoinwidget.R.drawable.ic_luna
import com.brentpanther.bitcoinwidget.R.drawable.ic_lunc
import com.brentpanther.bitcoinwidget.R.drawable.ic_mana
import com.brentpanther.bitcoinwidget.R.drawable.ic_matic
import com.brentpanther.bitcoinwidget.R.drawable.ic_mco
import com.brentpanther.bitcoinwidget.R.drawable.ic_mco_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_mkr
import com.brentpanther.bitcoinwidget.R.drawable.ic_mln
import com.brentpanther.bitcoinwidget.R.drawable.ic_nano
import com.brentpanther.bitcoinwidget.R.drawable.ic_near_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_near_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_neo
import com.brentpanther.bitcoinwidget.R.drawable.ic_nrg
import com.brentpanther.bitcoinwidget.R.drawable.ic_okb
import com.brentpanther.bitcoinwidget.R.drawable.ic_omg
import com.brentpanther.bitcoinwidget.R.drawable.ic_ont
import com.brentpanther.bitcoinwidget.R.drawable.ic_pax
import com.brentpanther.bitcoinwidget.R.drawable.ic_paxg_color
import com.brentpanther.bitcoinwidget.R.drawable.ic_placeholder
import com.brentpanther.bitcoinwidget.R.drawable.ic_powr_color
import com.brentpanther.bitcoinwidget.R.drawable.ic_ppc
import com.brentpanther.bitcoinwidget.R.drawable.ic_qtum
import com.brentpanther.bitcoinwidget.R.drawable.ic_rdd
import com.brentpanther.bitcoinwidget.R.drawable.ic_ren
import com.brentpanther.bitcoinwidget.R.drawable.ic_rep
import com.brentpanther.bitcoinwidget.R.drawable.ic_rune
import com.brentpanther.bitcoinwidget.R.drawable.ic_rvn
import com.brentpanther.bitcoinwidget.R.drawable.ic_sand
import com.brentpanther.bitcoinwidget.R.drawable.ic_shib
import com.brentpanther.bitcoinwidget.R.drawable.ic_snx
import com.brentpanther.bitcoinwidget.R.drawable.ic_sol
import com.brentpanther.bitcoinwidget.R.drawable.ic_storj
import com.brentpanther.bitcoinwidget.R.drawable.ic_sushi
import com.brentpanther.bitcoinwidget.R.drawable.ic_theta
import com.brentpanther.bitcoinwidget.R.drawable.ic_trx
import com.brentpanther.bitcoinwidget.R.drawable.ic_uma
import com.brentpanther.bitcoinwidget.R.drawable.ic_uni
import com.brentpanther.bitcoinwidget.R.drawable.ic_vet
import com.brentpanther.bitcoinwidget.R.drawable.ic_vtc
import com.brentpanther.bitcoinwidget.R.drawable.ic_waves
import com.brentpanther.bitcoinwidget.R.drawable.ic_wbtc
import com.brentpanther.bitcoinwidget.R.drawable.ic_xaut_color
import com.brentpanther.bitcoinwidget.R.drawable.ic_xem
import com.brentpanther.bitcoinwidget.R.drawable.ic_xem_dark_gray
import com.brentpanther.bitcoinwidget.R.drawable.ic_xlm
import com.brentpanther.bitcoinwidget.R.drawable.ic_xlm_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_xmr
import com.brentpanther.bitcoinwidget.R.drawable.ic_xmr_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_xrp_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_xrp_white
import com.brentpanther.bitcoinwidget.R.drawable.ic_xtz
import com.brentpanther.bitcoinwidget.R.drawable.ic_xvg
import com.brentpanther.bitcoinwidget.R.drawable.ic_xym
import com.brentpanther.bitcoinwidget.R.drawable.ic_yfi
import com.brentpanther.bitcoinwidget.R.drawable.ic_zec
import com.brentpanther.bitcoinwidget.R.drawable.ic_zec_dark
import com.brentpanther.bitcoinwidget.R.drawable.ic_zil
import com.brentpanther.bitcoinwidget.R.drawable.ic_zrx_black
import com.brentpanther.bitcoinwidget.R.drawable.ic_zrx_white
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
    ARRR("Pirate Chain", "pirate-chain", IconTheme(SOLID, ic_arrr)),
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
    CRO("Crypto.com Coin", "crypto-com-chain", IconTheme(SOLID, ic_cro, ic_cro_white)),
    CRV("Curve DAO Token", "curve-dao-token", IconTheme(SOLID, ic_crv)),
    CUBE("Somnium Space CUBEs", "somnium-space-cubes", IconTheme(SOLID, ic_cube_black, ic_cube_white)),
    DAI("Dai", "dai", IconTheme(SOLID, ic_dai_color)),
    DASH("Dash", "dash", IconTheme(SOLID, ic_dash, ic_dash_dark)),
    DCR("Decred", "decred", IconTheme(SOLID, ic_dcr)),
    DOGE("Dogecoin", "dogecoin", IconTheme(SOLID, ic_doge)),
    DOT("Polkadot", "polkadot", IconTheme(SOLID, ic_dot_black, ic_dot_white)),
    EGLD("Elrond", "elrond-erd-2", IconTheme(SOLID, ic_egld_dark, ic_egld_white)),
    ENJ("Enjin Coin", "enjincoin", IconTheme(SOLID, ic_enj)),
    EOS("EOS", "eos", IconTheme(SOLID, ic_eos_black, ic_eos_white), IconTheme(TRANSPARENT, ic_eos_white)),
    ETC("Ethereum Classic", "ethereum-classic", IconTheme(SOLID, ic_etc)),
    ETH("Ethereum", "ethereum", IconTheme(SOLID, ic_eth)),
    FIL("Filecoin", "filecoin", IconTheme(SOLID, ic_fil)),
    FIRO("Firo", "zcoin", IconTheme(SOLID, ic_firo, ic_firo_dark)),
    FTM("Fantom", "fantom", IconTheme(SOLID, ic_ftm)),
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
    IOTA("Iota", "iota", IconTheme(SOLID, ic_iota, ic_iota_white)),
    KAVA("Kava", "kava", IconTheme(SOLID, ic_kava)),
    KMD("Komodo", "komodo", IconTheme(SOLID, ic_kmd)),
    KNC("Kyber Network", "kyber-network", IconTheme(SOLID, ic_knc_color)),
    KSM("Kusama", "kusama", IconTheme(SOLID, ic_ksm_black, ic_ksm_white)),
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
    NEAR("Near", "near", IconTheme(SOLID, ic_near_black, ic_near_white)),
    NEO("NEO", "neo", IconTheme(SOLID, ic_neo)),
    NRG("Energi", "energi", IconTheme(SOLID, ic_nrg)),
    OKB("OKB", "okb", IconTheme(SOLID, ic_okb)),
    OMG("OMG", "omisego", IconTheme(SOLID, ic_omg)),
    ONT("Ontology", "ontology", IconTheme(SOLID, ic_ont)),
    PAX("Paxos Standard", "paxos-standard", IconTheme(SOLID, ic_pax)),
    PAXG("PAX Gold", "pax-gold", IconTheme(SOLID, ic_paxg_color)),
    POWR("Power Ledger", "power-ledger", IconTheme(SOLID, ic_powr_color)),
    PPC("Peercoin", "peercoin", IconTheme(SOLID, ic_ppc)),
    QTUM("Qtum", "qtum", IconTheme(SOLID, ic_qtum)),
    RDD("Reddcoin", "reddcoin", IconTheme(SOLID, ic_rdd)),
    REN("REN", "republic-protocol", IconTheme(SOLID, ic_ren)),
    REP("Augur", "augur", IconTheme(SOLID, ic_rep)),
    RUNE("THORChain", "thorchain", IconTheme(SOLID, ic_rune)),
    RVN("Ravencoin", "ravencoin", IconTheme(SOLID, ic_rvn)),
    SAND("The Sandbox", "the-sandbox", IconTheme(SOLID, ic_sand)),
    SHIB("Shiba Inu", "shiba-inu", IconTheme(SOLID, ic_shib)),
    SNX("Synthetix Network Token", "havven", IconTheme(SOLID, ic_snx)),
    SOL("Solana", "solana", IconTheme(SOLID, ic_sol)),
    STORJ("Storj", "storj", IconTheme(SOLID, ic_storj)),
    SUSHI("Sushi", "sushi", IconTheme(SOLID, ic_sushi)),
    THETA("Theta Network", "theta-token", IconTheme(SOLID, ic_theta)),
    TRX("Tron", "tron", IconTheme(SOLID, ic_trx)),
    UMA("UMA", "uma", IconTheme(SOLID, ic_uma)),
    UNI("Uniswap", "uniswap", IconTheme(SOLID, ic_uni)),
    VET("VeChain", "vechain", IconTheme(SOLID, ic_vet)),
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

        internal var COIN_NAMES: SortedSet<String> = values().map { it.name }.toSortedSet(String.CASE_INSENSITIVE_ORDER)

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
