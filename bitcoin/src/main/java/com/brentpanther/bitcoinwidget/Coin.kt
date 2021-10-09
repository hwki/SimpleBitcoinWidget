package com.brentpanther.bitcoinwidget

import android.os.Build
import android.os.Parcelable
import com.brentpanther.bitcoinwidget.R.drawable.*
import com.brentpanther.bitcoinwidget.Theme.SOLID
import com.brentpanther.bitcoinwidget.Theme.TRANSPARENT
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
enum class Coin(val coinName: String, vararg val themes: IconTheme) : Parcelable {

    CUSTOM("Custom", IconTheme(SOLID, ic_placeholder)),
    AAVE("Aave", IconTheme(SOLID, ic_aave)),
    ADA("Cardano", IconTheme(SOLID, ic_ada)),
    ALGO("Algorand", IconTheme(SOLID, ic_algo, ic_algo_white)),
    ARRR("Pirate Chain", IconTheme(SOLID, ic_arrr)),
    ATOM("Cosmos", IconTheme(SOLID, ic_atom)),
    AVA("Travala.com", IconTheme(SOLID, ic_ava)),
    AVAX("Avalanche", IconTheme(SOLID, ic_avax, ic_avax_dark)),
    AXS("Axie Infinity", IconTheme(SOLID, ic_axs)),
    BAL("Balancer", IconTheme(SOLID, ic_bal)),
    BAND("Band Protocol", IconTheme(SOLID, ic_band_color)),
    BAT("Basic Attention Token", IconTheme(SOLID, ic_bat)),
    BCD("Bitcoin Diamond", IconTheme(SOLID, ic_bcd, ic_bcd_white)),
    BCH("Bitcoin Cash", IconTheme(SOLID, ic_bch, ic_bch_dark)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("BCH", 1.0), CoinUnit("mBCH", .001), CoinUnit("μBCH", .000001))
        }
    },
    BNB("Binance Coin", IconTheme(SOLID, ic_bnb)),
    BEST("Bitpanda Ecosystem Token", IconTheme(SOLID, ic_best)),
    BNT("Bancor Network Token", IconTheme(SOLID, ic_bnt)),
    BSV("Bitcoin SV", IconTheme(SOLID, ic_bsv, ic_bsv_dark)),
    BTC("Bitcoin", IconTheme(SOLID, ic_btc, ic_btc_dark)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("BTC", 1.0), CoinUnit("mBTC", .001), CoinUnit("Bit", .000001), CoinUnit("Sat", .00000001))
        }
    },
    BTG("Bitcoin Gold", IconTheme(SOLID, ic_btg, ic_btg_dark)),
    BTM("Bytom", IconTheme(SOLID, ic_btm, ic_btm_gray)),
    BTT("BitTorrent", IconTheme(SOLID, ic_btt)),
    CEL("Celsius", IconTheme(SOLID, ic_cel)),
    CHZ("Chiliz", IconTheme(SOLID, ic_chz)),
    COMP("Compound", IconTheme(SOLID, ic_comp_black, ic_comp_white)),
    CRO("Crypto.com Coin", IconTheme(SOLID, ic_cro, ic_cro_white)),
    CRV("Curve DAO Token", IconTheme(SOLID, ic_crv)),
    DAI("Dai", IconTheme(SOLID, ic_dai_color)),
    DASH("Dash", IconTheme(SOLID, ic_dash, ic_dash_dark)),
    DCR("Decred", IconTheme(SOLID, ic_dcr)),
    DOGE("Dogecoin", IconTheme(SOLID, ic_doge)),
    DOT("Polkadot", IconTheme(SOLID, ic_dot_black, ic_dot_white)),
    EGLD("Elrond", IconTheme(SOLID, ic_egld_dark, ic_egld_white)),
    ENJ("Enjin Coin", IconTheme(SOLID, ic_enj)),
    EOS("EOS", IconTheme(SOLID, ic_eos_black, ic_eos_white), IconTheme(TRANSPARENT, ic_eos_white)),
    ETC("Ethereum Classic", IconTheme(SOLID, ic_etc)),
    ETH("Ethereum", IconTheme(SOLID, ic_eth)),
    FIL("Filecoin", IconTheme(SOLID, ic_fil)),
    FIRO("Firo", IconTheme(SOLID, ic_firo, ic_firo_dark)),
    FTT("FTX Token", IconTheme(SOLID, ic_ftt)),
    GNO("Gnosis", IconTheme(SOLID, ic_gno_color)),
    GNT("Golem", IconTheme(SOLID, ic_gnt_blue)),
    GRIN("Grin", IconTheme(SOLID, ic_grin_color_black)),
    GRT("The Graph", IconTheme(SOLID, ic_grt)),
    HBAR("Hedera Hashgraph", IconTheme(SOLID, ic_hbar, ic_hbar_white)),
    HNS("Handshake", IconTheme(SOLID, ic_hns, ic_hns_dark)),
    HT("Huobi Token", IconTheme(SOLID, ic_ht)),
    ICX("Icon", IconTheme(SOLID, ic_icx)),
    IOTA("Iota", IconTheme(SOLID, ic_iota, ic_iota_white)),
    KMD("Komodo", IconTheme(SOLID, ic_kmd)),
    KNC("Kyber Network", IconTheme(SOLID, ic_knc_color)),
    KSM("Kusama", IconTheme(SOLID, ic_ksm_black, ic_ksm_white)),
    LEO("LEO Token", IconTheme(SOLID, ic_leo)),
    LINK("ChainLink", IconTheme(SOLID, ic_link)),
    LRC("Loopring", IconTheme(SOLID, ic_lrc)),
    LSK("Lisk", IconTheme(SOLID, ic_lsk)),
    LTC("Litecoin", IconTheme(SOLID, ic_ltc)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("LTC", 1.0), CoinUnit("mŁ", .001), CoinUnit("μŁ", .0000001))
        }
    },
    LTO("LTO Network", IconTheme(SOLID, ic_lto)),
    LUNA("Terra", IconTheme(SOLID, ic_luna)),
    MANA("Decentraland", IconTheme(SOLID, ic_mana)),
    MATIC("Polygon", IconTheme(SOLID, ic_matic)),
    MCO("MCO", IconTheme(SOLID, ic_mco, ic_mco_white)),
    MKR("Maker", IconTheme(SOLID, ic_mkr)),
    MLN("Melon", IconTheme(SOLID, ic_mln)),
    NANO("Nano", IconTheme(SOLID, ic_nano)),
    NEAR("Near", IconTheme(SOLID, ic_near_black, ic_near_white)),
    NEO("NEO", IconTheme(SOLID, ic_neo)),
    NRG("Energi", IconTheme(SOLID, ic_nrg)),
    OKB("OKB", IconTheme(SOLID, ic_okb)),
    OMG("OMG", IconTheme(SOLID, ic_omg)),
    ONT("Ontology", IconTheme(SOLID, ic_ont)),
    PAX("Paxos Standard", IconTheme(SOLID, ic_pax)),
    PAXG("PAX Gold", IconTheme(SOLID, ic_paxg_color)),
    POWR("Power Ledger", IconTheme(SOLID, ic_powr_color)),
    PPC("Peercoin", IconTheme(SOLID, ic_ppc)),
    QTUM("Qtum", IconTheme(SOLID, ic_qtum)),
    RDD("Reddcoin", IconTheme(SOLID, ic_rdd)),
    REN("REN", IconTheme(SOLID, ic_ren)),
    REP("Augur", IconTheme(SOLID, ic_rep)),
    RUNE("THORChain", IconTheme(SOLID, ic_rune)),
    RVN("Ravencoin", IconTheme(SOLID, ic_rvn)),
    SHIB("Shiba Inu", IconTheme(SOLID, ic_shib)),
    SNX("Synthetix Network Token", IconTheme(SOLID, ic_snx)),
    SOL("Solana", IconTheme(SOLID, ic_sol)),
    SUSHI("Sushi", IconTheme(SOLID, ic_sushi)),
    THETA("Theta Network", IconTheme(SOLID, ic_theta)),
    TRX("Tron", IconTheme(SOLID, ic_trx)),
    UMA("UMA", IconTheme(SOLID, ic_uma)),
    UNI("Uniswap", IconTheme(SOLID, ic_uni)),
    VET("VeChain", IconTheme(SOLID, ic_vet)),
    VTC("Vertcoin", IconTheme(SOLID, ic_vtc)),
    WAVES("Waves", IconTheme(SOLID, ic_waves)),
    WBTC("Wrapped Bitcoin", IconTheme(SOLID, ic_wbtc)),
    XAUT("Tether Gold", IconTheme(SOLID, ic_xaut_color)),
    XEM("NEM", IconTheme(SOLID, ic_xem, ic_xem_dark_gray)),
    XLM("Stellar", IconTheme(SOLID, ic_xlm, ic_xlm_white)),
    XMR("Monero", IconTheme(SOLID, ic_xmr), IconTheme(TRANSPARENT, ic_xmr, dark = ic_xmr_dark)),
    XRP("Ripple", IconTheme(SOLID, ic_xrp_black, ic_xrp_white), IconTheme(TRANSPARENT, ic_xrp_white)),
    XTZ("Tezos", IconTheme(SOLID, ic_xtz)),
    XVG("Verge", IconTheme(SOLID, ic_xvg)),
    XYM("Symbol", IconTheme(SOLID, ic_xym)),
    YFI("yearn.finance", IconTheme(SOLID, ic_yfi)),
    ZEC("Zcash", IconTheme(SOLID, ic_zec, ic_zec_dark), IconTheme(TRANSPARENT, ic_zec)),
    ZIL("Zilliqa", IconTheme(SOLID, ic_zil)),
    ZRX("0x", IconTheme(SOLID, ic_zrx_black, ic_zrx_white));

    open fun getUnits() = emptyList<CoinUnit>()

    fun getUnitAmount(text: String) = getUnits().firstOrNull { it.text == text }?.amount ?: 1.0

    fun getIcon(theme: Theme, dark: Boolean): Int {
        val iconTheme = themes.firstOrNull { it.theme == theme } ?: themes.first()
        return if (dark) iconTheme.dark else iconTheme.light
    }

    companion object {

        internal var COIN_NAMES: SortedSet<String> = values().map { it.name }.toSortedSet(String.CASE_INSENSITIVE_ORDER)

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

@Parcelize
class CoinUnit(val text: String, val amount: Double) : Parcelable
