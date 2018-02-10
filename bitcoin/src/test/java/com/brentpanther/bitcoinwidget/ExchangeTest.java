package com.brentpanther.bitcoinwidget;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;


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
    public void removedCoins() throws Exception {
        for (Coin coin : Coin.values()) {
            ExchangeData data = new ExchangeData(coin, json);
            for (String currency : data.getCurrencies()) {
                for (String exchange : data.getExchanges(currency)) {
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
                    Thread.sleep(200);
                }
            }
        }
    }

    @Test
    public void allCoins() throws Exception {
        // some exchanges don't use coin/currency parameters, so skip them
        EnumSet<Exchange> skip = EnumSet.of(Exchange.BIT2C, Exchange.BITCOINDE, Exchange.BITHUMB, Exchange.BITPAY, Exchange.BTCBOX,
                Exchange.BTCTURK, Exchange.BTCXINDIA, Exchange.CAMPBX, Exchange.COINDESK, Exchange.COINNEST,
                Exchange.COINONE, Exchange.COINSECURE, Exchange.ETHEXINDIA, Exchange.KOINEX, Exchange.MERCADO,
                Exchange.PAYMIUM, Exchange.SIMPLECOINCZ, Exchange.ZYADO, Exchange.CHILEBIT, Exchange.FOXBIT,
                Exchange.SURBITCOIN, Exchange.URDUBIT, Exchange.VBTC);
        EnumSet<Exchange> exchangeSet = EnumSet.allOf(Exchange.class);
        exchangeSet.removeAll(skip);

        for (Coin coin : Coin.values()) {
            ExchangeData data = new ExchangeData(coin, json);
            Set<String> currencies = new HashSet<>(Arrays.asList(data.getCurrencies()));
            for (Currency currency : Currency.values()) {
                Set<String> exchanges = new HashSet<>(Arrays.asList(data.getExchanges(currency.name())));
                for (Exchange exchange : exchangeSet) {
                    if (exchange == Exchange.ZEBPAY && currency != Currency.INR) continue;
                    boolean exists = exchanges.contains(exchange.name());
                    String coinName = data.getExchangeCoinName(exchange.name(), coin.name());
                    String currencyName = data.getExchangeCurrencyName(exchange.name(), currency.name());
                    if (coinName == null) coinName = coin.name();
                    if (currencyName == null) currencyName = currency.name();
                    try {
                        String value = exchange.getValue(coinName, currencyName);
                        Double valueDouble = Double.valueOf(value);
                        if (valueDouble == 0) throw new ArithmeticException();
                        if (!exists) System.out.println(String.format("ADDED: %s %s %s", coin.name(), exchange, currency));
                    } catch (Exception e) {
                        if (exists) System.out.println(String.format("REMOVED: %s %s %s", coin.name(), exchange, currency));
                    }
                    Thread.sleep(100);
                }
            }
        }
    }

}