package com.brentpanther.bitcoinwidget

import android.os.Build

import androidx.annotation.DrawableRes
import com.brentpanther.bitcoinwidget.R.drawable.*
import com.brentpanther.bitcoinwidget.Themer.LIGHT as L
import com.brentpanther.bitcoinwidget.Themer.LIGHT_OLD as LO
import com.brentpanther.bitcoinwidget.Themer.DARK as D
import com.brentpanther.bitcoinwidget.Themer.DARK_OLD as DO
import com.brentpanther.bitcoinwidget.Themer.TRANSPARENT as T
import com.brentpanther.bitcoinwidget.Themer.TRANSPARENT_DARK as TD
import com.brentpanther.bitcoinwidget.Themer.TRANSPARENT_DARK_OLD as TDO
import java.io.Serializable
import java.util.*

enum class Coin(val coinName: String, @param:DrawableRes val icon: Int, internal val icons: Map<String, Int>) {

    CUSTOM("Custom", ic_placeholder, mapOf(L to ic_placeholder)),
    AAVE("Aave", ic_aave, mapOf(L to ic_aave, LO to ic_aave_gray)),
    ADA("Cardano", ic_ada, mapOf(L to ic_ada, LO to ic_ada_gray)),
    ALGO("Algorand", ic_algo, mapOf(L to ic_algo, LO to ic_algo_gray, D to ic_algo_white)),
    ARRR("Pirate Chain", ic_arrr, mapOf(L to ic_arrr, D to ic_arrr_gray)),
    ATOM("Cosmos", ic_atom, mapOf(L to ic_atom, LO to ic_atom_gray)),
    AVA("Travala.com", ic_ava, mapOf(L to ic_ava, LO to ic_ava_gray)),
    AVAX("Avalanche", ic_avax, mapOf(L to ic_avax, LO to ic_avax_gray, D to ic_avax_dark, DO to ic_avax_dark_gray)),
    BAL("Balancer", ic_bal, mapOf(L to ic_bal, LO to ic_bal_gray)),
    BAND("Band Protocol", ic_band_color, mapOf(L to ic_band_color, LO to ic_band_gray)),
    BAT("Basic Attention Token", ic_bat, mapOf(L to ic_bat, LO to ic_bat_gray)),
    BCD("Bitcoin Diamond", ic_bcd, mapOf(L to ic_bcd, LO to ic_bcd_gray, D to ic_bcd_white)),
    BCH("Bitcoin Cash", ic_bch, mapOf(L to ic_bch, LO to ic_bch_gray, D to ic_bch_dark, DO to ic_bch_dark_gray)) {
        override val units: List<Unit>
            get() = listOf(Unit("BCH", 1.0), Unit("mBCH", .001), Unit("μBCH", .000001))
    },
    BNB("Binance Coin", ic_bnb, mapOf(L to ic_bnb, LO to ic_bnb_gray)),
    BSV("Bitcoin SV", ic_bsv, mapOf(L to ic_bsv, LO to ic_bsv_gray, D to ic_bsv_dark, DO to ic_bsv_dark_gray)),
    BTC("Bitcoin", ic_btc, mapOf(L to ic_btc, LO to ic_btc_gray, D to ic_btc_dark, DO to ic_btc_dark_gray)) {
        override val units: List<Unit>
            get() = listOf(Unit("BTC", 1.0), Unit("mBTC", .001), Unit("μBTC / bits", .000001), Unit("Satoshis", .00000001))
    },
    BTG("Bitcoin Gold", ic_btg, mapOf(L to ic_btg, LO to ic_btg_gray, D to ic_btg_dark, DO to ic_btg_dark_gray)),
    BTM("Bytom", ic_btm, mapOf(L to ic_btm, LO to ic_btm_gray, D to ic_btm_gray)),
    BTT("BitTorrent", ic_btt, mapOf(L to ic_btt, LO to ic_btt_gray)),
    CEL("Celsius", ic_cel, mapOf(L to ic_cel, LO to ic_cel_gray)),
    COMP("Compound", ic_comp_black, mapOf(L to ic_comp_black, LO to ic_comp_gray, D to ic_comp_white)),
    CRO("Crypto.com Coin", ic_cro, mapOf(L to ic_cro, LO to ic_cro_gray, D to ic_cro_white)),
    DAI("Dai", ic_dai_color, mapOf(L to ic_dai_color, LO to ic_dai_gray)),
    DASH("Dash", ic_dash, mapOf(L to ic_dash, LO to ic_dash_gray, D to ic_dash_dark, DO to ic_dash_dark_gray)),
    DCR("Decred", ic_dcr, mapOf(L to ic_dcr, LO to ic_dcr_gray)),
    DOGE("Dogecoin", ic_doge, mapOf(L to ic_doge, LO to ic_doge_gray)),
    DOT("Polkadot", ic_dot_black, mapOf(L to ic_dot_black, LO to ic_dot_gray, D to ic_dot_white)),
    ENJ("Enjin Coin", ic_enj, mapOf(L to ic_enj, LO to ic_enj_gray)),
    EOS("EOS", ic_eos_black, mapOf(L to ic_eos_black, LO to ic_eos_gray, T to ic_eos_white, D to ic_eos_white)),
    ETC("Ethereum Classic", ic_etc, mapOf(L to ic_etc, LO to ic_etc_gray)),
    ETH("Ethereum", ic_eth, mapOf(L to ic_eth, LO to ic_eth_gray)),
    FIL("Filecoin", ic_fil, mapOf(L to ic_fil, LO to ic_fil_gray)),
    FIRO("Firo", ic_firo, mapOf(L to ic_firo, LO to ic_firo_gray, D to ic_firo_dark, DO to ic_firo_dark_gray)),
    FTT("FTX Token", ic_ftt, mapOf(L to ic_ftt, LO to ic_ftt_gray)),
    GNO("Gnosis", ic_gno_color, mapOf(L to ic_gno_color, LO to ic_gno_gray)),
    GNT("Golem", ic_gnt_blue, mapOf(L to ic_gnt_blue, LO to ic_gnt_gray)),
    GRIN("Grin", ic_grin_color_black, mapOf(L to ic_grin_color_black, LO to ic_grin_gray_white, D to ic_grin_color_black, DO to ic_grin_gray_black)),
    HBAR("Hedera Hashgraph", ic_hbar, mapOf(L to ic_hbar, LO to ic_hbar_gray, D to ic_hbar_white)),
    HNS("Handshake", ic_hns, mapOf(L to ic_hns, LO to ic_hns_gray, D to ic_hns_dark)),
    HT("Huobi Token", ic_ht, mapOf(L to ic_ht, LO to ic_ht_gray)),
    ICX("Icon", ic_icx, mapOf(L to ic_icx, LO to ic_icx_gray)),
    IOTA("Iota", ic_iota, mapOf(L to ic_iota, LO to ic_iota_gray, D to ic_iota_white)),
    KMD("Komodo", ic_kmd, mapOf(L to ic_kmd, LO to ic_kmd_gray)),
    KNC("Kyber Network", ic_knc_color, mapOf(L to ic_knc_color, LO to ic_knc_gray)),
    KSM("Kusama", ic_ksm_black, mapOf(L to ic_ksm_black, LO to ic_ksm_gray, D to ic_ksm_white)),
    LEO("LEO Token", ic_leo, mapOf(L to ic_leo, LO to ic_leo_gray)),
    LINK("ChainLink", ic_link, mapOf(L to ic_link, LO to ic_link_gray)),
    LRC("Loopring", ic_lrc, mapOf(L to ic_lrc, LO to ic_lrc_gray)),
    LSK("Lisk", ic_lsk, mapOf(L to ic_lsk, LO to ic_lsk_gray)),
    LTC("Litecoin", ic_ltc, mapOf(L to ic_ltc, LO to ic_ltc_gray)) {
        override val units: List<Unit>
            get() = listOf(Unit("LTC", 1.0), Unit("lites", .001))
    },
    LTO("LTO Network", ic_lto, mapOf(L to ic_lto, LO to ic_lto_gray)),
    MANA("Decentraland", ic_mana, mapOf(L to ic_mana, LO to ic_mana_gray)),
    MCO("MCO", ic_mco, mapOf(L to ic_mco, LO to ic_mco_gray, D to ic_mco_white)),
    MKR("Maker", ic_mkr, mapOf(L to ic_mkr, LO to ic_mkr_gray)),
    MLN("Melon", ic_mln, mapOf(L to ic_mln, LO to ic_mln_gray)),
    NANO("Nano", ic_nano, mapOf(L to ic_nano, LO to ic_nano_gray)),
    NEAR("Near", ic_near_black, mapOf(L to ic_near_black, LO to ic_near_gray, D to ic_near_white)),
    NEO("NEO", ic_neo, mapOf(L to ic_neo, LO to ic_neo_gray)),
    NRG("Energi", ic_nrg, mapOf(L to ic_nrg, LO to ic_nrg_gray)),
    OKB("OKB", ic_okb, mapOf(L to ic_okb, LO to ic_okb_gray)),
    OMG("OMG", ic_omg, mapOf(L to ic_omg, LO to ic_omg_gray)),
    ONT("Ontology", ic_ont, mapOf(L to ic_ont, LO to ic_ont_gray)),
    PAX("Paxos Standard", ic_pax, mapOf(L to ic_pax, LO to ic_paxg_gray)),
    PAXG("PAX Gold", ic_paxg_color, mapOf(L to ic_paxg_color, LO to ic_paxg_gray)),
    POWR("Power Ledger", ic_powr_color, mapOf(L to ic_powr_color, LO to ic_powr_gray)),
    PPC("Peercoin", ic_ppc, mapOf(L to ic_ppc, LO to ic_ppc_gray)),
    QTUM("Qtum", ic_qtum, mapOf(L to ic_qtum, LO to ic_qtum_gray)),
    RDD("Reddcoin", ic_rdd, mapOf(L to ic_rdd, LO to ic_rdd_gray)),
    REN("REN", ic_ren, mapOf(L to ic_ren, LO to ic_ren_gray)),
    REP("Augur", ic_rep, mapOf(L to ic_rep, LO to ic_rep_gray)),
    RUNE("THORChain", ic_rune, mapOf(L to ic_rune, LO to ic_rune_gray)),
    RVN("Ravencoin", ic_rvn, mapOf(L to ic_rvn, LO to ic_rvn_gray)),
    SNX("Synthetix Network Token", ic_snx, mapOf(L to ic_snx, LO to ic_snx_gray)),
    SOL("Solana", ic_sol, mapOf(L to ic_sol, LO to ic_sol_gray)),
    SUSHI("Sushi", ic_sushi, mapOf(L to ic_sushi, LO to ic_sushi_gray)),
    THETA("Theta Network", ic_theta, mapOf(L to ic_theta, LO to ic_theta_gray)),
    TRX("Tron", ic_trx, mapOf(L to ic_trx, LO to ic_trx_gray)),
    UNI("Uniswap", ic_uni, mapOf(L to ic_uni, LO to ic_uni_gray)),
    VET("VeChain", ic_vet, mapOf(L to ic_vet, LO to ic_vet_gray)),
    VTC("Vertcoin", ic_vtc, mapOf(L to ic_vtc, LO to ic_vtc_gray)),
    WAVES("Waves", ic_waves, mapOf(L to ic_waves, LO to ic_waves_gray)),
    XAUT("Tether Gold", ic_xaut_color, mapOf(L to ic_xaut_color, LO to ic_xaut_gray)),
    XEM("NEM", ic_xem, mapOf(L to ic_xem, LO to ic_xem_gray, D to ic_xem_dark, DO to ic_xem_dark_gray)),
    XLM("Stellar", ic_xlm, mapOf(L to ic_xlm, LO to ic_xlm_gray, D to ic_xlm_white)),
    XMR("Monero", ic_xmr, mapOf(L to ic_xmr, LO to ic_xmr_gray, TD to ic_xmr_dark)),
    XRP("Ripple", ic_xrp_black, mapOf(L to ic_xrp_black, LO to ic_xrp_gray, T to ic_xrp_white, D to ic_xrp_white)),
    XTZ("Tezos", ic_xtz, mapOf(L to ic_xtz, LO to ic_xtz_gray)),
    XVG("Verge", ic_xvg, mapOf(L to ic_xvg, LO to ic_xvg_gray)),
    YFI("yearn.finance", ic_yfi, mapOf(L to ic_yfi, LO to ic_yfi_gray)),
    ZEC("Zcash", ic_zec, mapOf(L to ic_zec, LO to ic_zec_gray, D to ic_zec_dark, DO to ic_zec_dark_gray,
            TD to ic_zec, TDO to ic_zec)),
    ZIL("Zilliqa", ic_zil, mapOf(L to ic_zil, LO to ic_zil_gray)),
    ZRX("0x", ic_zrx_black, mapOf(L to ic_zrx_black, LO to ic_zrx_gray, D to ic_zrx_white));

    protected open val units: List<Unit> = emptyList()

    val unitNames: Array<String>
        get() = units.map { it.text}.toTypedArray()

    fun getUnitAmount(text: String) = units.firstOrNull { it.text == text }?.amount ?: 1.0

    companion object {

        internal var COIN_NAMES: SortedSet<String> = values().map { it.name }.toSortedSet(String.CASE_INSENSITIVE_ORDER)

        fun getVirtualCurrencyFormat(currency: String, hideSymbol: Boolean): String {
            return when (currency) {
                "BTC" ->
                    // bitcoin symbol added in Oreo
                    if (Build.VERSION.SDK_INT >= 26) "₿ #,###" else "Ƀ #,###"
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

class Unit(val text: String, val amount: Double) : Serializable
