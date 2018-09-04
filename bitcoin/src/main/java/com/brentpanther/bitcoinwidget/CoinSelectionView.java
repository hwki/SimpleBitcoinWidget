package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CoinSelectionView extends LinearLayout {

    private Coin coin;
    private CoinSelectedListener listener;

    public CoinSelectionView(Context context) {
        super(context);
        inflate(context, R.layout.view_coin_selector, this);
        int height = (int)getResources().getDimension(R.dimen.coin_selection_height);
        MarginLayoutParams layoutParams = new MarginLayoutParams(LayoutParams.MATCH_PARENT, height);
        int verticalMargin = (int) getResources().getDimension(R.dimen.vertical_margin);
        int horizontalMargin = (int) getResources().getDimension(R.dimen.horizontal_margin);
        layoutParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        setLayoutParams(layoutParams);
        setOnClickListener(v -> listener.selected(coin));
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

    interface CoinSelectedListener {
        void selected(Coin coin);
    }
}
