package com.brentpanther.cryptowidget;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

/**
 * Created by brentpanther on 5/7/17.
 */

public abstract class Ids {

    protected abstract @LayoutRes int widgetLayout();

    protected abstract @LayoutRes int widgetLayoutTransparent();

    protected abstract @IdRes int price();

    protected abstract @IdRes int provider();

    protected abstract @IdRes int parent();

    protected abstract @IdRes int loading();

    protected abstract @IdRes int topSpace();

    protected abstract @IdRes int image();

    protected abstract @IdRes int imageBW();

}
