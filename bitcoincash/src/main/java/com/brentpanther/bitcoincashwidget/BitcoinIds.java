package com.brentpanther.bitcoincashwidget;

import com.brentpanther.cryptowidget.Ids;

/**
 * Created by brentpanther on 5/7/17.
 */

public class BitcoinIds extends Ids {

    @Override
    protected int widgetLayout() {
        return R.layout.widget_layout;
    }

    @Override
    protected int widgetLayoutTransparent() {
        return R.layout.widget_layout_transparent;
    }

    @Override
    protected int price() {
        return R.id.price;
    }

    @Override
    protected int provider() {
        return R.id.provider;
    }

    @Override
    protected int parent() {
        return R.id.bitcoinParent;
    }

    @Override
    protected int loading() {
        return R.id.loading;
    }

    @Override
    protected int topSpace() {
        return R.id.top_space;
    }

    @Override
    protected int image() {
        return R.id.bitcoinImage;
    }

    @Override
    protected int imageBW() {
        return R.id.bitcoinImageBW;
    }


}
