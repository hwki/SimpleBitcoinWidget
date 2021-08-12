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
    ADA("Cardano", IconTheme(SOLID, ic_ada, ic_ada_gray), IconTheme(TRANSPARENT, ic_ada, ic_ada_gray)),
    ALGO("Algorand", IconTheme(SOLID, ic_algo, ic_algo_gray, ic_algo_white), IconTheme(TRANSPARENT, ic_algo, ic_algo_gray, ic_algo_white)),
    ARRR("Pirate Chain", IconTheme(SOLID, ic_arrr, ic_arrr_gray), IconTheme(TRANSPARENT, ic_arrr, ic_arrr_gray)),
    ATOM("Cosmos", IconTheme(SOLID, ic_atom, ic_atom_gray), IconTheme(TRANSPARENT, ic_atom, ic_atom_gray)),
    AVA("Travala.com", IconTheme(SOLID, ic_ava, ic_ava_gray), IconTheme(TRANSPARENT, ic_ava, ic_ava_gray)),
    AVAX("Avalanche", IconTheme(SOLID, ic_avax, ic_avax_gray, ic_avax_dark, ic_avax_dark_gray), IconTheme(TRANSPARENT, ic_avax, ic_avax_gray, ic_avax_dark, ic_avax_dark_gray)),
    BAL("Balancer", IconTheme(SOLID, ic_bal, ic_bal_gray), IconTheme(TRANSPARENT, ic_bal, ic_bal_gray)),
    BAND("Band Protocol", IconTheme(SOLID, ic_band_color, ic_band_gray), IconTheme(TRANSPARENT, ic_band_color, ic_band_gray)),
    BAT("Basic Attention Token", IconTheme(SOLID, ic_bat, ic_bat_gray), IconTheme(TRANSPARENT, ic_bat, ic_bat_gray)),
    BCD("Bitcoin Diamond", IconTheme(SOLID, ic_bcd, ic_bcd_gray, ic_bcd_white), IconTheme(TRANSPARENT, ic_bcd, ic_bcd_gray, ic_bcd_white)),
    BCH("Bitcoin Cash", IconTheme(SOLID, ic_bch, ic_bch_gray, ic_bch_dark, ic_bch_dark_gray), IconTheme(TRANSPARENT, ic_bch, ic_bch_gray, ic_bch_dark, ic_bch_dark_gray)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("BCH", 1.0), CoinUnit("mBCH", .001), CoinUnit("μBCH", .000001))
        }
    },
    BNB("Binance Coin", IconTheme(SOLID, ic_bnb, ic_bnb_gray), IconTheme(TRANSPARENT, ic_bnb, ic_bnb_gray)),
    BSV("Bitcoin SV", IconTheme(SOLID, ic_bsv, ic_bsv_gray, ic_bsv_dark, ic_bsv_dark_gray), IconTheme(TRANSPARENT, ic_bsv, ic_bsv_gray, ic_bsv_dark, ic_bsv_dark_gray)),
    BTC("Bitcoin", IconTheme(SOLID, ic_btc, ic_btc_gray, ic_btc_dark, ic_btc_dark_gray), IconTheme(TRANSPARENT, ic_btc, ic_btc_gray, ic_btc_dark, ic_btc_dark_gray)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("BTC", 1.0), CoinUnit("mBTC", .001), CoinUnit("μBTC / bits", .000001), CoinUnit("Satoshis", .00000001))
        }
    },
    BTG("Bitcoin Gold", IconTheme(SOLID, ic_btg, ic_btg_gray, ic_btg_dark, ic_btg_dark_gray), IconTheme(TRANSPARENT, ic_btg, ic_btg_gray, ic_btg_dark, ic_btg_dark_gray)),
    BTM("Bytom", IconTheme(SOLID, ic_btm, ic_btm_gray, ic_btm_gray), IconTheme(TRANSPARENT, ic_btm, ic_btm_gray, ic_btm_gray)),
    BTT("BitTorrent", IconTheme(SOLID, ic_btt, ic_btt_gray), IconTheme(TRANSPARENT, ic_btt, ic_btt_gray)),
    CEL("Celsius", IconTheme(SOLID, ic_cel, ic_cel_gray), IconTheme(TRANSPARENT, ic_cel, ic_cel_gray)),
    COMP("Compound", IconTheme(SOLID, ic_comp_black, ic_comp_gray, ic_comp_white), IconTheme(TRANSPARENT, ic_comp_black, ic_comp_gray, ic_comp_white)),
    CRO("Crypto.com Coin", IconTheme(SOLID, ic_cro, ic_cro_gray, ic_cro_white), IconTheme(TRANSPARENT, ic_cro, ic_cro_gray, ic_cro_white)),
    DAI("Dai", IconTheme(SOLID, ic_dai_color, ic_dai_gray), IconTheme(TRANSPARENT, ic_dai_color, ic_dai_gray)),
    DASH("Dash", IconTheme(SOLID, ic_dash, ic_dash_gray, ic_dash_dark, ic_dash_dark_gray), IconTheme(TRANSPARENT, ic_dash, ic_dash_gray, ic_dash_dark, ic_dash_dark_gray)),
    DCR("Decred", IconTheme(SOLID, ic_dcr, ic_dcr_gray), IconTheme(TRANSPARENT, ic_dcr, ic_dcr_gray)),
    DOGE("Dogecoin", IconTheme(SOLID, ic_doge, ic_doge_gray), IconTheme(TRANSPARENT, ic_doge, ic_doge_gray)),
    DOT("Polkadot", IconTheme(SOLID, ic_dot_black, ic_dot_gray, ic_dot_white), IconTheme(TRANSPARENT, ic_dot_black, ic_dot_gray, ic_dot_white)),
    ENJ("Enjin Coin", IconTheme(SOLID, ic_enj, ic_enj_gray), IconTheme(TRANSPARENT, ic_enj, ic_enj_gray)),
    EOS("EOS", IconTheme(SOLID, ic_eos_black, ic_eos_gray, ic_eos_white), IconTheme(TRANSPARENT, ic_eos_white, ic_eos_gray)),
    ETC("Ethereum Classic", IconTheme(SOLID, ic_etc, ic_etc_gray), IconTheme(TRANSPARENT, ic_etc, ic_etc_gray)),
    ETH("Ethereum", IconTheme(SOLID, ic_eth, ic_eth_gray), IconTheme(TRANSPARENT, ic_eth, ic_eth_gray)),
    FIL("Filecoin", IconTheme(SOLID, ic_fil, ic_fil_gray), IconTheme(TRANSPARENT, ic_fil, ic_fil_gray)),
    FIRO("Firo", IconTheme(SOLID, ic_firo, ic_firo_gray, ic_firo_dark, ic_firo_dark_gray), IconTheme(TRANSPARENT,  ic_firo, ic_firo_gray, ic_firo_dark, ic_firo_dark_gray)),
    FTT("FTX Token", IconTheme(SOLID, ic_ftt, ic_ftt_gray), IconTheme(TRANSPARENT, ic_ftt, ic_ftt_gray)),
    GNO("Gnosis", IconTheme(SOLID, ic_gno_color, ic_gno_gray), IconTheme(TRANSPARENT, ic_gno_color, ic_gno_gray)),
    GNT("Golem", IconTheme(SOLID, ic_gnt_blue, ic_gnt_gray), IconTheme(TRANSPARENT, ic_gnt_blue, ic_gnt_gray)),
    GRIN("Grin", IconTheme(SOLID, ic_grin_color_black, ic_grin_gray_white, ic_grin_color_black, ic_grin_gray_black), IconTheme(TRANSPARENT, ic_grin_color_black, ic_grin_gray_white, ic_grin_color_black, ic_grin_gray_black)),
    HBAR("Hedera Hashgraph", IconTheme(SOLID, ic_hbar, ic_hbar_gray, ic_hbar_white), IconTheme(TRANSPARENT, ic_hbar, ic_hbar_gray, ic_hbar_white)),
    HNS("Handshake", IconTheme(SOLID, ic_hns, ic_hns_gray, ic_hns_dark), IconTheme(TRANSPARENT, ic_hns, ic_hns_gray, ic_hns_dark)),
    HT("Huobi Token", IconTheme(SOLID, ic_ht, ic_ht_gray), IconTheme(TRANSPARENT, ic_ht, ic_ht_gray)),
    ICX("Icon", IconTheme(SOLID, ic_icx, ic_icx_gray), IconTheme(TRANSPARENT, ic_icx, ic_icx_gray)),
    IOTA("Iota", IconTheme(SOLID, ic_iota, ic_iota_gray, ic_iota_white), IconTheme(TRANSPARENT, ic_iota, ic_iota_gray, ic_iota_white)),
    KMD("Komodo", IconTheme(SOLID, ic_kmd, ic_kmd_gray), IconTheme(TRANSPARENT, ic_kmd, ic_kmd_gray)),
    KNC("Kyber Network", IconTheme(SOLID, ic_knc_color, ic_knc_gray), IconTheme(TRANSPARENT, ic_knc_color, ic_knc_gray)),
    KSM("Kusama", IconTheme(SOLID, ic_ksm_black, ic_ksm_gray, ic_ksm_white), IconTheme(TRANSPARENT, ic_ksm_black, ic_ksm_gray, ic_ksm_white)),
    LEO("LEO Token", IconTheme(SOLID, ic_leo, ic_leo_gray), IconTheme(TRANSPARENT, ic_leo, ic_leo_gray)),
    LINK("ChainLink", IconTheme(SOLID, ic_link, ic_link_gray), IconTheme(TRANSPARENT, ic_link, ic_link_gray)),
    LRC("Loopring", IconTheme(SOLID, ic_lrc, ic_lrc_gray), IconTheme(TRANSPARENT, ic_lrc, ic_lrc_gray)),
    LSK("Lisk", IconTheme(SOLID, ic_lsk, ic_lsk_gray), IconTheme(TRANSPARENT, ic_lsk, ic_lsk_gray)),
    LTC("Litecoin", IconTheme(SOLID, ic_ltc, ic_ltc_gray), IconTheme(TRANSPARENT, ic_ltc, ic_ltc_gray)) {
        override fun getUnits(): List<CoinUnit> {
            return listOf(CoinUnit("LTC", 1.0), CoinUnit("lites", .001))
        }
    },
    LTO("LTO Network", IconTheme(SOLID, ic_lto, ic_lto_gray), IconTheme(TRANSPARENT, ic_lto, ic_lto_gray)),
    MANA("Decentraland", IconTheme(SOLID, ic_mana, ic_mana_gray), IconTheme(TRANSPARENT, ic_mana, ic_mana_gray)),
    MATIC("Polygon", IconTheme(SOLID, ic_matic, ic_matic_gray), IconTheme(TRANSPARENT, ic_matic, ic_matic_gray)),
    MCO("MCO", IconTheme(SOLID, ic_mco, ic_mco_gray, ic_mco_white), IconTheme(TRANSPARENT, ic_mco, ic_mco_gray, ic_mco_white)),
    MKR("Maker", IconTheme(SOLID, ic_mkr, ic_mkr_gray), IconTheme(TRANSPARENT, ic_mkr, ic_mkr_gray)),
    MLN("Melon", IconTheme(SOLID, ic_mln, ic_mln_gray), IconTheme(TRANSPARENT, ic_mln, ic_mln_gray)),
    NANO("Nano", IconTheme(SOLID, ic_nano, ic_nano_gray), IconTheme(TRANSPARENT, ic_nano, ic_nano_gray)),
    NEAR("Near", IconTheme(SOLID, ic_near_black, ic_near_gray, ic_near_white), IconTheme(TRANSPARENT, ic_near_black, ic_near_gray, ic_near_white)),
    NEO("NEO", IconTheme(SOLID, ic_neo, ic_neo_gray), IconTheme(TRANSPARENT, ic_neo, ic_neo_gray)),
    NRG("Energi", IconTheme(SOLID, ic_nrg, ic_nrg_gray), IconTheme(TRANSPARENT, ic_nrg, ic_nrg_gray)),
    OKB("OKB", IconTheme(SOLID, ic_okb, ic_okb_gray), IconTheme(TRANSPARENT, ic_okb, ic_okb_gray)),
    OMG("OMG", IconTheme(SOLID, ic_omg, ic_omg_gray), IconTheme(TRANSPARENT, ic_omg, ic_omg_gray)),
    ONT("Ontology", IconTheme(SOLID, ic_ont, ic_ont_gray), IconTheme(TRANSPARENT, ic_ont, ic_ont_gray)),
    PAX("Paxos Standard", IconTheme(SOLID, ic_pax, ic_paxg_gray), IconTheme(TRANSPARENT, ic_pax, ic_paxg_gray)),
    PAXG("PAX Gold", IconTheme(SOLID, ic_paxg_color, ic_paxg_gray), IconTheme(TRANSPARENT, ic_paxg_color, ic_paxg_gray)),
    POWR("Power Ledger", IconTheme(SOLID, ic_powr_color, ic_powr_gray), IconTheme(TRANSPARENT, ic_powr_color, ic_powr_gray)),
    PPC("Peercoin", IconTheme(SOLID, ic_ppc, ic_ppc_gray), IconTheme(TRANSPARENT, ic_ppc, ic_ppc_gray)),
    QTUM("Qtum", IconTheme(SOLID, ic_qtum, ic_qtum_gray), IconTheme(TRANSPARENT, ic_qtum, ic_qtum_gray)),
    RDD("Reddcoin", IconTheme(SOLID, ic_rdd, ic_rdd_gray), IconTheme(TRANSPARENT, ic_rdd, ic_rdd_gray)),
    REN("REN", IconTheme(SOLID, ic_ren, ic_ren_gray), IconTheme(TRANSPARENT, ic_ren, ic_ren_gray)),
    REP("Augur", IconTheme(SOLID, ic_rep, ic_rep_gray), IconTheme(TRANSPARENT, ic_rep, ic_rep_gray)),
    RUNE("THORChain", IconTheme(SOLID, ic_rune, ic_rune_gray), IconTheme(TRANSPARENT, ic_rune, ic_rune_gray)),
    RVN("Ravencoin", IconTheme(SOLID, ic_rvn, ic_rvn_gray), IconTheme(TRANSPARENT, ic_rvn, ic_rvn_gray)),
    SNX("Synthetix Network Token", IconTheme(SOLID, ic_snx, ic_snx_gray), IconTheme(TRANSPARENT, ic_snx, ic_snx_gray)),
    SOL("Solana", IconTheme(SOLID, ic_sol, ic_sol_gray), IconTheme(TRANSPARENT, ic_sol, ic_sol_gray)),
    SUSHI("Sushi", IconTheme(SOLID, ic_sushi, ic_sushi_gray), IconTheme(TRANSPARENT, ic_sushi, ic_sushi_gray)),
    THETA("Theta Network", IconTheme(SOLID, ic_theta, ic_theta_gray), IconTheme(TRANSPARENT, ic_theta, ic_theta_gray)),
    TRX("Tron", IconTheme(SOLID, ic_trx, ic_trx_gray), IconTheme(TRANSPARENT, ic_trx, ic_trx_gray)),
    UNI("Uniswap", IconTheme(SOLID, ic_uni, ic_uni_gray), IconTheme(TRANSPARENT, ic_uni, ic_uni_gray)),
    VET("VeChain", IconTheme(SOLID, ic_vet, ic_vet_gray), IconTheme(TRANSPARENT, ic_vet, ic_vet_gray)),
    VTC("Vertcoin", IconTheme(SOLID, ic_vtc, ic_vtc_gray), IconTheme(TRANSPARENT, ic_vtc, ic_vtc_gray)),
    WAVES("Waves", IconTheme(SOLID, ic_waves, ic_waves_gray), IconTheme(TRANSPARENT, ic_waves, ic_waves_gray)),
    XAUT("Tether Gold", IconTheme(SOLID, ic_xaut_color, ic_xaut_gray), IconTheme(TRANSPARENT, ic_xaut_color, ic_xaut_gray)),
    XEM("NEM", IconTheme(SOLID, ic_xem, ic_xem_dark, ic_xem_dark_gray), IconTheme(TRANSPARENT, ic_xem, ic_xem_dark, ic_xem_dark_gray)),
    XLM("Stellar", IconTheme(SOLID, ic_xlm, ic_xlm_gray, ic_xlm_white), IconTheme(TRANSPARENT, ic_xlm, ic_xlm_gray, ic_xlm_white)),
    XMR("Monero", IconTheme(SOLID, ic_xmr, ic_xmr_gray), IconTheme(TRANSPARENT, ic_xmr, dark = ic_xmr_dark)),
    XRP("Ripple", IconTheme(SOLID, ic_xrp_black, ic_xrp_gray, ic_xrp_white), IconTheme(TRANSPARENT, ic_xrp_white, ic_xrp_gray)),
    XTZ("Tezos", IconTheme(SOLID, ic_xtz, ic_xtz_gray), IconTheme(TRANSPARENT, ic_xtz, ic_xtz_gray)),
    XVG("Verge", IconTheme(SOLID, ic_xvg, ic_xvg_gray), IconTheme(TRANSPARENT, ic_xvg, ic_xvg_gray)),
    YFI("yearn.finance", IconTheme(SOLID, ic_yfi, ic_yfi_gray), IconTheme(TRANSPARENT, ic_yfi, ic_yfi_gray)),
    ZEC("Zcash", IconTheme(SOLID, ic_zec, ic_zec_gray, ic_zec_dark, ic_zec_dark_gray), IconTheme(TRANSPARENT, ic_zec, ic_zec_gray)),
    ZIL("Zilliqa", IconTheme(SOLID, ic_zil, ic_zil_gray), IconTheme(TRANSPARENT, ic_zil, ic_zil_gray)),
    ZRX("0x", IconTheme(SOLID, ic_zrx_black, ic_zrx_gray, ic_zrx_white), IconTheme(TRANSPARENT, ic_zrx_black, ic_zrx_gray, ic_zrx_white));

    open fun getUnits() = emptyList<CoinUnit>()

    fun getUnitAmount(text: String) = getUnits().firstOrNull { it.text == text }?.amount ?: 1.0

    fun getIcon(theme: Theme, dark: Boolean, old: Boolean): Int {
        val iconTheme = themes.first{ it.theme == theme }
        return when {
            dark && old -> iconTheme.darkOld
            dark -> iconTheme.dark
            old -> iconTheme.lightOld
            else -> iconTheme.light
        }
    }

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

@Parcelize
class CoinUnit(val text: String, val amount: Double) : Parcelable
