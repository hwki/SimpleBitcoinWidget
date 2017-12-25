package com.brentpanther.bitcoinwidget;


class Unit {

    private final String text;
    private final double amount;

    Unit(String text, double amount) {
        this.text = text;
        this.amount = amount;
    }

    public String getText() {
        return text;
    }

    double getAmount() {
        return amount;
    }
}
