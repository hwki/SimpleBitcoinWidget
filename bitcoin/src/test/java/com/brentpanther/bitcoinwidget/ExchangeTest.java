package com.brentpanther.bitcoinwidget;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;


public class ExchangeTest {

    private String json;

    @Before
    public void loadJSON() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("raw/cryptowidgetcoins.json");
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        this.json = new String(buffer, "UTF-8");
    }

    @Test
    public void json() throws Exception {
        Coin[] coins = Coin.values();
        for (Coin coin : coins) {
            ExchangeData data = new ExchangeData(coin, json);
            String[] currencies = data.getCurrencies();
            for (String currency : currencies) {
                String[] exchanges = data.getExchanges(currency);
                for (String exchange : exchanges) {
                    try {
                        String value = Exchange.valueOf(exchange).getValue(coin.name(), currency);
                        Double.valueOf(value);
                    } catch (Exception e) {
                        System.out.println(String.format("Failure: %s %s %s", coin.name(), exchange, currency));
                    }
                    Thread.sleep(200);
                }
            }
        }
    }

}