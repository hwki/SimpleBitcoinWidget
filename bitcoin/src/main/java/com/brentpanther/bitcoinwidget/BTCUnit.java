package com.brentpanther.bitcoinwidget;

import com.brentpanther.cryptowidget.Unit;

/**
 * Created by Panther on 2/11/2017.
 */

public enum BTCUnit implements Unit {

    BTC(1),
    mBTC(0.001d),
    μBTC(0.000001d);

    private double conversion;

    BTCUnit(double conversion) {
        this.conversion = conversion;
    }

    public double adjust(String amount) {
        return Double.valueOf(amount) * this.conversion;
    }
}
