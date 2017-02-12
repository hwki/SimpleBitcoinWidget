package com.brentpanther.bitcoinwidget;

/**
 * Created by Panther on 2/11/2017.
 */

enum Unit {
    BTC(1),
    mBTC(0.001d),
    μBTC(0.000001d);

    private double conversion;

    Unit(double conversion) {
        this.conversion = conversion;
    }

    public double adjust(String amount) {
        return Double.valueOf(amount) * this.conversion;
    }
}
