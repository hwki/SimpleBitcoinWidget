package com.brentpanther.bitcoinwidget;


import android.os.Build;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.DrawableRes;

public enum Coin {

    BTC("Bitcoin", R.drawable.ic_btc) {
        @Override
        protected List<Unit> getUnits() {
            return Arrays.asList(
                    new Unit("BTC", 1),
                    new Unit("mBTC", .001d),
                    new Unit("μBTC", .000001d));
        }
        @Override
        public int[] getDrawables() {
            return new int[]{R.drawable.ic_btc, R.drawable.ic_btc_bw, R.drawable.ic_btc_dark, R.drawable.ic_btc_dark_bw};
        }
    },
    ETH("Ethereum", R.drawable.ic_eth_color) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_eth_color, R.drawable.ic_eth, R.drawable.ic_eth_color, R.drawable.ic_eth};
        }
    },
    XRP("Ripple", R.drawable.ic_xrp) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_xrp, R.drawable.ic_xrp_bw, R.drawable.ic_xrp, R.drawable.ic_xrp_bw};
        }
    },
    BCH("Bitcoin Cash", R.drawable.ic_bch) {
        @Override
        protected List<Unit> getUnits() {
            return Arrays.asList(
                    new Unit("BCH", 1),
                    new Unit("mBCH", .001d),
                    new Unit("μBCH", .000001d));
        }

        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_bch, R.drawable.ic_bch_bw, R.drawable.ic_bch_dark, R.drawable.ic_bch_dark_bw};
        }
    },
    LTC("Litecoin", R.drawable.ic_ltc) {
        @Override
        protected List<Unit> getUnits() {
            return Arrays.asList(
                    new Unit("LTC", 1),
                    new Unit("lites", .001d));
        }

        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_ltc_color, R.drawable.ic_ltc, R.drawable.ic_ltc_color, R.drawable.ic_ltc};
        }
    },
    NEO("NEO", R.drawable.ic_neo) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_neo, R.drawable.ic_neo_bw, R.drawable.ic_neo, R.drawable.ic_neo_bw};
        }
    },
    ADA("Cardano", R.drawable.ic_ada) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_ada, R.drawable.ic_ada_bw, R.drawable.ic_ada, R.drawable.ic_ada_bw};
        }
    },
    XLM("Stellar", R.drawable.ic_xlm) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_xlm, R.drawable.ic_xlm_bw, R.drawable.ic_xlm, R.drawable.ic_xlm_bw};
        }
    },
    IOTA("Iota", R.drawable.ic_iota) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_iota, R.drawable.ic_iota_bw, R.drawable.ic_iota, R.drawable.ic_iota_bw};
        }
    },
    DASH("Dash", R.drawable.ic_dash) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_dash, R.drawable.ic_dash_bw, R.drawable.ic_dash_dark, R.drawable.ic_dash_dark_bw};
        }
    },
    XMR("Monero", R.drawable.ic_xrm) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_xrm, R.drawable.ic_xrm_bw, R.drawable.ic_xrm, R.drawable.ic_xrm_bw};
        }
    },
    XEM("NEM", R.drawable.ic_xem) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_xem, R.drawable.ic_xem_bw, R.drawable.ic_xem, R.drawable.ic_xem_bw};
        }
    },
    NANO("Nano", R.drawable.ic_nano) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_nano, R.drawable.ic_nano_bw, R.drawable.ic_nano, R.drawable.ic_nano_bw};
        }
    },
    BTG("Bitcoin Gold", R.drawable.ic_btg) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_btg, R.drawable.ic_btg_bw, R.drawable.ic_btg, R.drawable.ic_btg_bw};
        }
    },
    ETC("Ethereum Classic", R.drawable.ic_etc) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_etc, R.drawable.ic_etc_bw, R.drawable.ic_etc, R.drawable.ic_etc_bw};
        }
    },
    ZEC("Zcash", R.drawable.ic_zec) {
        @Override
        public int[] getDrawables() {
            return new int[]{R.drawable.ic_zec, R.drawable.ic_zec_bw, R.drawable.ic_zec, R.drawable.ic_zec_bw};
        }
    };

    static Set<String> COIN_NAMES = new TreeSet<>();

    static {
        for (Coin coin : Coin.values()) {
            COIN_NAMES.add(coin.name());
        }
    }

    private final String name;
    private final int icon;

    Coin(String name, @DrawableRes int icon) {
        this.name = name;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    @DrawableRes
    public abstract int[] getDrawables();

    protected List<Unit> getUnits() {
        return Collections.emptyList();
    }

    public String[] getUnitNames() {
        List<Unit> units = getUnits();
        String[] unitNames = new String[units.size()];
        for (int i = 0; i < units.size(); i++) {
            unitNames[i] = units.get(i).getText();
        }
        return unitNames;
    }

    public double getUnitAmount(String text) {
        List<Unit> units = getUnits();
        for (Unit unit : units) {
            if (unit.getText().equals(text)) {
                return unit.getAmount();
            }
        }
        return 1;
    }

    public static String getVirtualCurrencyFormat(String currency) {
        switch (currency) {
            case "BTC":
                // bitcoin symbol added in Oreo
                return Build.VERSION.SDK_INT >= 26 ? "₿ #,###" : "Ƀ #,###";
            case "LTC":
                return "Ł #,###";
            default:
                return String.format("#,### %s", currency);
        }
    }

}
