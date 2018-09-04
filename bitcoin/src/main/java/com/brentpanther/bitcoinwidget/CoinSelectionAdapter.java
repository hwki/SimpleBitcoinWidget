package com.brentpanther.bitcoinwidget;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CoinSelectionAdapter extends RecyclerView.Adapter<CoinSelectionAdapter.ViewHolder> {

    private CoinSelectionView.CoinSelectedListener listener;
    private final ArrayList<Coin> coins;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(CoinSelectionView v) {
            super(v);
        }
    }

    CoinSelectionAdapter(CoinSelectionView.CoinSelectedListener listener) {
        this.listener = listener;
        coins = new ArrayList<>(EnumSet.allOf(Coin.class));
        Collections.sort(coins, (o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    @NonNull
    @Override
    public CoinSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(new CoinSelectionView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((CoinSelectionView)holder.itemView).setCoin(coins.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return Coin.values().length;
    }
}