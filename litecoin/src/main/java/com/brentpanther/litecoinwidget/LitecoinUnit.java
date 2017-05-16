package com.brentpanther.litecoinwidget;

import com.brentpanther.cryptowidget.Unit;

/**
 * Created by brentpanther on 5/10/17.
 */

public enum LitecoinUnit implements Unit {

    LTC(1),
    mLTC(0.001d),
    μLTC(0.000001d);

    private double conversion;

    LitecoinUnit(double conversion) {
        this.conversion = conversion;
    }

    public double adjust(String amount) {
        return Double.valueOf(amount) * this.conversion;
    }
}
