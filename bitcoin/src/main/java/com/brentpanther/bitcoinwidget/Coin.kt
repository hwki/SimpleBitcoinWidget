package com.brentpanther.bitcoinwidget


import android.os.Build

import androidx.annotation.DrawableRes
import com.brentpanther.bitcoinwidget.R.drawable.*
import java.util.*

enum class Coin constructor(val coinName: String, @param:DrawableRes val icon: Int) {

    BTC("Bitcoin", ic_btc) {
        override val units: List<Unit>
            get() = listOf(Unit("BTC", 1.0), Unit("mBTC", .001), Unit("μBTC", .000001))
        override val drawables: IntArray
            get() = intArrayOf(ic_btc, ic_btc_bw, ic_btc_dark, ic_btc_dark_bw)
    },
    ETH("Ethereum", ic_eth_color) {
        override val drawables: IntArray
            get() = intArrayOf(ic_eth_color, ic_eth, ic_eth_color, ic_eth)
    },
    XRP("Ripple", ic_xrp) {
        override val drawables: IntArray
            get() = intArrayOf(ic_xrp, ic_xrp_bw, ic_xrp, ic_xrp_bw)
    },
    BCH("Bitcoin Cash", ic_bch) {
        override val units: List<Unit>
            get() = listOf(Unit("BCH", 1.0), Unit("mBCH", .001), Unit("μBCH", .000001))

        override val drawables: IntArray
            get() = intArrayOf(ic_bch, ic_bch_bw, ic_bch_dark, ic_bch_dark_bw)
    },
    LTC("Litecoin", ic_ltc) {
        override val units: List<Unit>
            get() = listOf(Unit("LTC", 1.0), Unit("lites", .001))

        override val drawables: IntArray
            get() = intArrayOf(ic_ltc_color, ic_ltc, ic_ltc_color, ic_ltc)
    },
    NEO("NEO", ic_neo) {
        override val drawables: IntArray
            get() = intArrayOf(ic_neo, ic_neo_bw, ic_neo, ic_neo_bw)
    },
    ADA("Cardano", ic_ada) {
        override val drawables: IntArray
            get() = intArrayOf(ic_ada, ic_ada_bw, ic_ada, ic_ada_bw)
    },
    XLM("Stellar", ic_xlm) {
        override val drawables: IntArray
            get() = intArrayOf(ic_xlm, ic_xlm, ic_xlm, ic_xlm)
    },
    IOTA("Iota", ic_iota) {
        override val drawables: IntArray
            get() = intArrayOf(ic_iota, ic_iota_bw, ic_iota, ic_iota_bw)
    },
    DASH("Dash", ic_dash) {
        override val drawables: IntArray
            get() = intArrayOf(ic_dash, ic_dash_bw, ic_dash_dark, ic_dash_dark_bw)
    },
    XMR("Monero", ic_xrm) {
        override val drawables: IntArray
            get() = intArrayOf(ic_xrm, ic_xrm_bw, ic_xrm, ic_xrm_bw)
    },
    XEM("NEM", ic_xem) {
        override val drawables: IntArray
            get() = intArrayOf(ic_xem, ic_xem_bw, ic_xem, ic_xem_bw)
    },
    NANO("Nano", ic_nano) {
        override val drawables: IntArray
            get() = intArrayOf(ic_nano, ic_nano_bw, ic_nano, ic_nano_bw)
    },
    BTG("Bitcoin Gold", ic_btg) {
        override val drawables: IntArray
            get() = intArrayOf(ic_btg, ic_btg_bw, ic_btg, ic_btg_bw)
    },
    ETC("Ethereum Classic", ic_etc) {
        override val drawables: IntArray
            get() = intArrayOf(ic_etc, ic_etc_bw, ic_etc, ic_etc_bw)
    },
    ZEC("Zcash", ic_zec) {
        override val drawables: IntArray
            get() = intArrayOf(ic_zec, ic_zec_bw, ic_zec, ic_zec_bw)
    },
    XVG("Verge", ic_xvg) {
        override val drawables: IntArray
            get() = intArrayOf(ic_xvg, ic_xvg_bw, ic_xvg, ic_xvg_bw)
    },
    DOGE("Dogecoin", ic_doge) {
        override val drawables: IntArray
            get() = intArrayOf(ic_doge, ic_doge_bw, ic_doge, ic_doge_bw)
    },
    DCR("Decred", ic_dcr) {
        override val drawables: IntArray
            get() = intArrayOf(ic_dcr, ic_dcr_bw, ic_dcr, ic_dcr_bw)
    },
    PPC("Peercoin", ic_ppc) {
        override val drawables: IntArray
            get() = intArrayOf(ic_ppc, ic_ppc_bw, ic_ppc, ic_ppc_bw)
    },
    VTC("Vertcoin", ic_vtc) {
        override val drawables: IntArray
            get() = intArrayOf(ic_vtc, ic_vtc_bw, ic_vtc, ic_vtc_bw)
    },
    TRX("Tron", ic_trx) {
        override val drawables: IntArray
            get() = intArrayOf(ic_trx, ic_trx_bw, ic_trx, ic_trx_bw)
    },
    RDD("Reddcoin", ic_rdd) {
        override val drawables: IntArray
            get() = intArrayOf(ic_rdd, ic_rdd_bw, ic_rdd, ic_rdd_bw)
    },
    XTZ("Tezos", ic_xtz) {
        override val drawables: IntArray
            get() = intArrayOf(ic_xtz, ic_xtz_bw, ic_xtz, ic_xtz_bw)
    },
    BNB("Binance Coin", ic_bnb) {
        override val drawables: IntArray
            get() = intArrayOf(ic_bnb, ic_bnb, ic_bnb, ic_bnb)
    };

    @get:DrawableRes
    abstract val drawables: IntArray

    protected open val units: List<Unit>
        get() = emptyList()

    val unitNames: Array<String>
        get() = units.map { it.text}.toTypedArray()

    fun getUnitAmount(text: String): Double {
        return units.firstOrNull { it.text == text }?.amount ?: 1.0
    }

    companion object {

        internal var COIN_NAMES: SortedSet<String> = values().map { it.name }.toSortedSet()

        fun getVirtualCurrencyFormat(currency: String): String {
            return when (currency) {
                "BTC" ->
                    // bitcoin symbol added in Oreo
                    if (Build.VERSION.SDK_INT >= 26) "₿ #,###" else "Ƀ #,###"
                "LTC" -> "Ł #,###"
                else -> String.format("#,### %s", currency)
            }
        }
    }

}
