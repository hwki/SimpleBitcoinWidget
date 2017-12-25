package com.brentpanther.bitcoinwidget;

import android.support.annotation.DrawableRes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    BCH("Bitcoin Cash", R.drawable.ic_bch) {
        @Override
        protected List<Unit> getUnits() {
            return Arrays.asList(
                    new Unit("BTC", 1),
                    new Unit("mBTC", .001d),
                    new Unit("μBTC", .000001d));
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
    IOTA("Iota", R.drawable.ic_iota) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_iota, R.drawable.ic_iota_bw, R.drawable.ic_iota, R.drawable.ic_iota_bw};
        }
    },
    XRP("Ripple", R.drawable.ic_xrp) {
        @Override
        public int[] getDrawables() {
            return new int[] {R.drawable.ic_xrp, R.drawable.ic_xrp_bw, R.drawable.ic_xrp, R.drawable.ic_xrp_bw};
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
    };

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
}
