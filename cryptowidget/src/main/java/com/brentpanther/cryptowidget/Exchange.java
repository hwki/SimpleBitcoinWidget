package com.brentpanther.cryptowidget;

/**
 * Created by brentpanther on 5/4/17.
 */

public interface Exchange {

    String getValue(String currencyCode) throws Exception;

    int getCurrencies();

    String getLabel();
}
