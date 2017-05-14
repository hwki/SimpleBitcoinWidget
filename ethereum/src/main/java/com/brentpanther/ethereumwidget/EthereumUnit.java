package com.brentpanther.ethereumwidget;

import com.brentpanther.cryptowidget.Unit;

/**
 * Created by brentpanther on 5/10/17.
 */

public enum EthereumUnit implements Unit {

    ETH;

    @Override
    public double adjust(String amount) {
        return Double.valueOf(amount);
    }
}
