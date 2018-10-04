package com.brentpanther.bitcoinwidget;

import org.junit.Test;

import java.io.InputStream;
import java.util.EnumSet;


public class ExchangeTest {

    private InputStream loadJSON() {
        return this.getClass().getClassLoader().getResourceAsStream("raw/cryptowidgetcoins.json");
    }

    @Test
    public void removedCoins() throws Exception {
        EnumSet<Coin> coins = EnumSet.allOf(Coin.class);
        for (Coin coin : coins) {
            System.out.println("trying coin: " + coin.name());
            ExchangeData data = new ExchangeData(coin, loadJSON());
            for (String currency : data.getCurrencies()) {
                for (String exchange : data.getExchanges(currency)) {
                    if (exchange.equals("COINMARKETCAP")) continue;
                    if (exchange.equals("BITCOIN_AVERAGE")) continue;
                    if (exchange.equals("BITCOIN_AVERAGE_GLOBAL")) continue;
                    try {
                        String coinName = data.getExchangeCoinName(exchange, coin.name());
                        String currencyName = data.getExchangeCurrencyName(exchange, currency);
                        if (coinName == null) coinName = coin.name();
                        if (currencyName == null) currencyName = currency;
                        String value = Exchange.valueOf(exchange).getValue(coinName, currencyName);
                        Double.valueOf(value);
                    } catch (Exception e) {
                        System.out.println(String.format("Failure: %s %s %s", coin.name(), exchange, currency));
                    }
                    Thread.sleep(100);
                }
            }
        }
    }

}