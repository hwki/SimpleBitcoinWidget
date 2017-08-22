package com.brentpanther.bitcoincashwidget;

import com.brentpanther.cryptowidget.Unit;

/**
 * Created by Panther on 2/11/2017.
 */

public enum BCHUnit implements Unit {

    BCH(1),
    mBCH(0.001d),
    μBCH(0.000001d);

    private double conversion;

    BCHUnit(double conversion) {
        this.conversion = conversion;
    }

    public double adjust(String amount) {
        return Double.valueOf(amount) * this.conversion;
    }
}
