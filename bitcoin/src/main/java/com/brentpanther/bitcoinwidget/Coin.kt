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

    CUSTOM("Custom", IconTheme(SOLID, ic_placeholder), IconTheme(TRANSPARENT, ic_placeholder)),
    AAVE("Aave", IconTheme(SOLID, ic_aave), IconTheme(TRANSPARENT, ic_aave)),
    ADA("Cardano", IconTheme(SOLID, ic_ada), IconTheme(TRANSPARENT, ic_ada)),
    ALGO("Algorand", IconTheme(SOLID, ic_algo, ic_algo_white), IconTheme(TRANSPARENT, ic_algo, ic_algo_white)),
    ARRR("Pirate Chain", IconTheme(SOLID, ic_arrr), IconTheme(TRANSPARENT, ic_arrr)),
    ATOM("Cosmos", IconTheme(SOLID, ic_atom), IconTheme(TRANSPARENT, ic_atom)),
    AVA("Travala.com", IconTheme(SOLID, ic_ava), IconTheme(TRANSPARENT, ic_ava)),
    AVAX("Avalanche", IconTheme(SOLID, ic_avax, ic_avax_dark), IconTheme(TRANSPARENT, ic_avax, ic_avax_dark)),
    BAL("Balancer", IconTheme(SOLID, ic_bal), IconTheme(TRANSPARENT, ic_bal)),
    BAND("Band Protocol", IconTheme(SOLID, ic_band_color), IconTheme(TRANSPARENT, ic_band_color)),
    BAT("Basic Attention Token", IconTheme(SOLID, ic_bat), IconTheme(TRANSPARENT, ic_bat)),
    BCD("Bitcoin Diamond", IconTheme(SOLID, ic_bcd, ic_bcd_white), IconTheme(TRANSPARENT, ic_bcd, ic_bcd_white)),
    BCH("Bitcoin Cash", IconTheme(SOLID, ic_bch, ic_bch_dark), IconTheme(TRANSPARENT, ic_bch, ic_bch_dark)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("BCH", 1.0), CoinUnit("mBCH", .001), CoinUnit("μBCH", .000001))
        }
    },
    BNB("Binance Coin", IconTheme(SOLID, ic_bnb), IconTheme(TRANSPARENT, ic_bnb)),
    BSV("Bitcoin SV", IconTheme(SOLID, ic_bsv, ic_bsv_dark), IconTheme(TRANSPARENT, ic_bsv, ic_bsv_dark)),
    BTC("Bitcoin", IconTheme(SOLID, ic_btc, ic_btc_dark), IconTheme(TRANSPARENT, ic_btc, ic_btc_dark)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("BTC", 1.0), CoinUnit("mBTC", .001), CoinUnit("Bit", .000001), CoinUnit("Sat", .00000001))
        }
    },
    BTG("Bitcoin Gold", IconTheme(SOLID, ic_btg, ic_btg_dark), IconTheme(TRANSPARENT, ic_btg, ic_btg_dark)),
    BTM("Bytom", IconTheme(SOLID, ic_btm, ic_btm_gray), IconTheme(TRANSPARENT, ic_btm, ic_btm_gray)),
    BTT("BitTorrent", IconTheme(SOLID, ic_btt), IconTheme(TRANSPARENT, ic_btt)),
    CEL("Celsius", IconTheme(SOLID, ic_cel), IconTheme(TRANSPARENT, ic_cel)),
    COMP("Compound", IconTheme(SOLID, ic_comp_black, ic_comp_white), IconTheme(
        TRANSPARENT,
        ic_comp_black,
        ic_comp_white
    )),
    CRO("Crypto.com Coin", IconTheme(SOLID, ic_cro, ic_cro_white), IconTheme(TRANSPARENT, ic_cro, ic_cro_white)),
    DAI("Dai", IconTheme(SOLID, ic_dai_color), IconTheme(TRANSPARENT, ic_dai_color)),
    DASH("Dash", IconTheme(SOLID, ic_dash, ic_dash_dark), IconTheme(TRANSPARENT, ic_dash, ic_dash_dark)),
    DCR("Decred", IconTheme(SOLID, ic_dcr), IconTheme(TRANSPARENT, ic_dcr)),
    DOGE("Dogecoin", IconTheme(SOLID, ic_doge), IconTheme(TRANSPARENT, ic_doge)),
    DOT("Polkadot", IconTheme(SOLID, ic_dot_black, ic_dot_white), IconTheme(TRANSPARENT, ic_dot_black, ic_dot_white)),
    ENJ("Enjin Coin", IconTheme(SOLID, ic_enj), IconTheme(TRANSPARENT, ic_enj)),
    EOS("EOS", IconTheme(SOLID, ic_eos_black, ic_eos_white), IconTheme(TRANSPARENT, ic_eos_white)),
    ETC("Ethereum Classic", IconTheme(SOLID, ic_etc), IconTheme(TRANSPARENT, ic_etc)),
    ETH("Ethereum", IconTheme(SOLID, ic_eth), IconTheme(TRANSPARENT, ic_eth)),
    FIL("Filecoin", IconTheme(SOLID, ic_fil), IconTheme(TRANSPARENT, ic_fil)),
    FIRO("Firo", IconTheme(SOLID, ic_firo, ic_firo_dark), IconTheme(TRANSPARENT, ic_firo, ic_firo_dark)),
    FTT("FTX Token", IconTheme(SOLID, ic_ftt), IconTheme(TRANSPARENT, ic_ftt)),
    GNO("Gnosis", IconTheme(SOLID, ic_gno_color), IconTheme(TRANSPARENT, ic_gno_color)),
    GNT("Golem", IconTheme(SOLID, ic_gnt_blue), IconTheme(TRANSPARENT, ic_gnt_blue)),
    GRIN("Grin", IconTheme(SOLID, ic_grin_color_black, ic_grin_color_black), IconTheme(
        TRANSPARENT,
        ic_grin_color_black,
        ic_grin_color_black
    )),
    HBAR("Hedera Hashgraph", IconTheme(SOLID, ic_hbar, ic_hbar_white), IconTheme(TRANSPARENT, ic_hbar, ic_hbar_white)),
    HNS("Handshake", IconTheme(SOLID, ic_hns, ic_hns_dark), IconTheme(TRANSPARENT, ic_hns, ic_hns_dark)),
    HT("Huobi Token", IconTheme(SOLID, ic_ht), IconTheme(TRANSPARENT, ic_ht)),
    ICX("Icon", IconTheme(SOLID, ic_icx), IconTheme(TRANSPARENT, ic_icx)),
    IOTA("Iota", IconTheme(SOLID, ic_iota, ic_iota_white), IconTheme(TRANSPARENT, ic_iota, ic_iota_white)),
    KMD("Komodo", IconTheme(SOLID, ic_kmd), IconTheme(TRANSPARENT, ic_kmd)),
    KNC("Kyber Network", IconTheme(SOLID, ic_knc_color), IconTheme(TRANSPARENT, ic_knc_color)),
    KSM("Kusama", IconTheme(SOLID, ic_ksm_black, ic_ksm_white), IconTheme(TRANSPARENT, ic_ksm_black, ic_ksm_white)),
    LEO("LEO Token", IconTheme(SOLID, ic_leo), IconTheme(TRANSPARENT, ic_leo)),
    LINK("ChainLink", IconTheme(SOLID, ic_link), IconTheme(TRANSPARENT, ic_link)),
    LRC("Loopring", IconTheme(SOLID, ic_lrc), IconTheme(TRANSPARENT, ic_lrc)),
    LSK("Lisk", IconTheme(SOLID, ic_lsk), IconTheme(TRANSPARENT, ic_lsk)),
    LTC("Litecoin", IconTheme(SOLID, ic_ltc), IconTheme(TRANSPARENT, ic_ltc)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("LTC", 1.0), CoinUnit("mŁ", .001), CoinUnit("μŁ", .0000001))
        }
    },
    LTO("LTO Network", IconTheme(SOLID, ic_lto), IconTheme(TRANSPARENT, ic_lto)),
    MANA("Decentraland", IconTheme(SOLID, ic_mana), IconTheme(TRANSPARENT, ic_mana)),
    MATIC("Polygon", IconTheme(SOLID, ic_matic), IconTheme(TRANSPARENT, ic_matic)),
    MCO("MCO", IconTheme(SOLID, ic_mco, ic_mco_white), IconTheme(TRANSPARENT, ic_mco, ic_mco_white)),
    MKR("Maker", IconTheme(SOLID, ic_mkr), IconTheme(TRANSPARENT, ic_mkr)),
    MLN("Melon", IconTheme(SOLID, ic_mln), IconTheme(TRANSPARENT, ic_mln)),
    NANO("Nano", IconTheme(SOLID, ic_nano), IconTheme(TRANSPARENT, ic_nano)),
    NEAR("Near", IconTheme(SOLID, ic_near_black, ic_near_white), IconTheme(TRANSPARENT, ic_near_black, ic_near_white)),
    NEO("NEO", IconTheme(SOLID, ic_neo), IconTheme(TRANSPARENT, ic_neo)),
    NRG("Energi", IconTheme(SOLID, ic_nrg), IconTheme(TRANSPARENT, ic_nrg)),
    OKB("OKB", IconTheme(SOLID, ic_okb), IconTheme(TRANSPARENT, ic_okb)),
    OMG("OMG", IconTheme(SOLID, ic_omg), IconTheme(TRANSPARENT, ic_omg)),
    ONT("Ontology", IconTheme(SOLID, ic_ont), IconTheme(TRANSPARENT, ic_ont)),
    PAX("Paxos Standard", IconTheme(SOLID, ic_pax), IconTheme(TRANSPARENT, ic_pax)),
    PAXG("PAX Gold", IconTheme(SOLID, ic_paxg_color), IconTheme(TRANSPARENT, ic_paxg_color)),
    POWR("Power Ledger", IconTheme(SOLID, ic_powr_color), IconTheme(TRANSPARENT, ic_powr_color)),
    PPC("Peercoin", IconTheme(SOLID, ic_ppc), IconTheme(TRANSPARENT, ic_ppc)),
    QTUM("Qtum", IconTheme(SOLID, ic_qtum), IconTheme(TRANSPARENT, ic_qtum)),
    RDD("Reddcoin", IconTheme(SOLID, ic_rdd), IconTheme(TRANSPARENT, ic_rdd)),
    REN("REN", IconTheme(SOLID, ic_ren), IconTheme(TRANSPARENT, ic_ren)),
    REP("Augur", IconTheme(SOLID, ic_rep), IconTheme(TRANSPARENT, ic_rep)),
    RUNE("THORChain", IconTheme(SOLID, ic_rune), IconTheme(TRANSPARENT, ic_rune)),
    RVN("Ravencoin", IconTheme(SOLID, ic_rvn), IconTheme(TRANSPARENT, ic_rvn)),
    SNX("Synthetix Network Token", IconTheme(SOLID, ic_snx), IconTheme(TRANSPARENT, ic_snx)),
    SOL("Solana", IconTheme(SOLID, ic_sol), IconTheme(TRANSPARENT, ic_sol)),
    SUSHI("Sushi", IconTheme(SOLID, ic_sushi), IconTheme(TRANSPARENT, ic_sushi)),
    THETA("Theta Network", IconTheme(SOLID, ic_theta), IconTheme(TRANSPARENT, ic_theta)),
    TRX("Tron", IconTheme(SOLID, ic_trx), IconTheme(TRANSPARENT, ic_trx)),
    UNI("Uniswap", IconTheme(SOLID, ic_uni), IconTheme(TRANSPARENT, ic_uni)),
    VET("VeChain", IconTheme(SOLID, ic_vet), IconTheme(TRANSPARENT, ic_vet)),
    VTC("Vertcoin", IconTheme(SOLID, ic_vtc), IconTheme(TRANSPARENT, ic_vtc)),
    WAVES("Waves", IconTheme(SOLID, ic_waves), IconTheme(TRANSPARENT, ic_waves)),
    XAUT("Tether Gold", IconTheme(SOLID, ic_xaut_color), IconTheme(TRANSPARENT, ic_xaut_color)),
    XEM("NEM", IconTheme(SOLID, ic_xem, ic_xem_dark_gray), IconTheme(TRANSPARENT, ic_xem, ic_xem_dark_gray)),
    XLM("Stellar", IconTheme(SOLID, ic_xlm, ic_xlm_white), IconTheme(TRANSPARENT, ic_xlm, ic_xlm_white)),
    XMR("Monero", IconTheme(SOLID, ic_xmr), IconTheme(TRANSPARENT, ic_xmr, dark = ic_xmr_dark)),
    XRP("Ripple", IconTheme(SOLID, ic_xrp_black, ic_xrp_white), IconTheme(TRANSPARENT, ic_xrp_white)),
    XTZ("Tezos", IconTheme(SOLID, ic_xtz), IconTheme(TRANSPARENT, ic_xtz)),
    XVG("Verge", IconTheme(SOLID, ic_xvg), IconTheme(TRANSPARENT, ic_xvg)),
    YFI("yearn.finance", IconTheme(SOLID, ic_yfi), IconTheme(TRANSPARENT, ic_yfi)),
    ZEC("Zcash", IconTheme(SOLID, ic_zec, ic_zec_dark), IconTheme(TRANSPARENT, ic_zec)),
    ZIL("Zilliqa", IconTheme(SOLID, ic_zil), IconTheme(TRANSPARENT, ic_zil)),
    ZRX("0x", IconTheme(SOLID, ic_zrx_black, ic_zrx_white), IconTheme(TRANSPARENT, ic_zrx_black, ic_zrx_white));

    open fun getUnits() = emptyList<CoinUnit>()

    fun getUnitAmount(text: String) = getUnits().firstOrNull { it.text == text }?.amount ?: 1.0

    fun getIcon(theme: Theme, dark: Boolean): Int {
        val iconTheme = themes.first{ it.theme == theme }
        return if (dark) iconTheme.dark else iconTheme.light
    }

    companion object {

        internal var COIN_NAMES: SortedSet<String> = values().map { it.name }.toSortedSet(String.CASE_INSENSITIVE_ORDER)

        fun getVirtualCurrencyFormat(currency: String, hideSymbol: Boolean): String {
            return when (currency) {
                "BTC" ->
                    // bitcoin symbol added in Oreo
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "₿ #,###" else "Ƀ #,###"
                "LTC" -> "Ł #,###"
                "DOGE" -> "Ð #,###"
                else -> {
                    if (hideSymbol) {
                        "#,###"
                    } else {
                        "#,### $currency"
                    }
                }
            }
        }
    }

}

@Parcelize
class CoinUnit(val text: String, val amount: Double) : Parcelable
