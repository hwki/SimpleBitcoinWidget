package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CoinSelectionView extends LinearLayout implements View.OnClickListener {

    private Coin coin;
    private CoinSelectedListener listener;

    public CoinSelectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_coin_selector, this);
        setOnClickListener(this);
        setBackgroundResource(R.drawable.bg_rounded);
    }

    public void setCoin(Coin coin, CoinSelectedListener listener) {
        this.coin = coin;
        this.listener = listener;
        TextView name = findViewById(R.id.coin_name);
        name.setText(coin.getName());
        ImageView image = findViewById(R.id.coin_icon);
        image.setImageResource(coin.getIcon());
    }

    @Override
    public void onClick(View v) {
        listener.selected(coin);
    }

    interface CoinSelectedListener {
        void selected(Coin coin);
    }
}
