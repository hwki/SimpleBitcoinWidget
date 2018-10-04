package com.brentpanther.bitcoinwidget;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ExchangeData {

    private static Map<String, List<String>> currencyExchange;

    class JsonExchangeObject {

        List<JsonExchange> exchanges;

        void loadCurrencies(String coin) {
            for (JsonExchange exchange: exchanges) {
                List<String> currencies = exchange.loadExchange(coin);
                for (String currency : currencies) {
                    if (!currencyExchange.containsKey(currency)) {
                        currencyExchange.put(currency, new ArrayList<>());
                    }
                    currencyExchange.get(currency).add(exchange.name);
                }
            }
        }

        String getExchangeCoinName(String exchange, String coin) {
            for (JsonExchange jsonExchange : exchanges) {
                if (!jsonExchange.name.equals(exchange)) continue;
                if (jsonExchange.coin_overrides != null) {
                    return jsonExchange.coin_overrides.get(coin);
                }
            }
            return null;
        }

        String getExchangeCurrencyName(String exchange, String currency) {
            for (JsonExchange jsonExchange : exchanges) {
                if (!jsonExchange.name.equals(exchange)) continue;
                if (jsonExchange.currency_overrides != null) {
                    return jsonExchange.currency_overrides.get(currency);
                }
            }
            return null;
        }
    }

    class JsonExchange {

        String name;
        List<JsonCoin> coins;
        Map<String, String> currency_overrides;
        Map<String, String> coin_overrides;

        List<String> loadExchange(String coin) {
            for (JsonCoin jsonCoin : coins) {
                if (!jsonCoin.name.equals(coin)) continue;
                return jsonCoin.currencies;
            }
            return new ArrayList<>();
        }
    }

    class JsonCoin {
        String name;
        List<String> currencies;
    }

    private final Coin coin;
    private final JsonExchangeObject obj;
    private static final List<String> CURRENCY_TOP_ORDER = Arrays.asList("USD", "EUR", "BTC");

    ExchangeData(Coin coin, InputStream json) {
        this.coin = coin;
        this.obj = new Gson().fromJson(new InputStreamReader(json), JsonExchangeObject.class);
        currencyExchange = new HashMap<>();
        this.obj.loadCurrencies(coin.name());
    }

    String[] getCurrencies() {
        // only return currencies that we know about
        List<String> currencyNames = new ArrayList<>();
        for (Currency currency : Currency.getAvailableCurrencies()) {
            currencyNames.add(currency.getCurrencyCode());
        }
        currencyNames.addAll(Coin.COIN_NAMES);
        currencyNames.retainAll(currencyExchange.keySet());
        Collections.sort(currencyNames, (o1, o2) -> {
            int i1 = CURRENCY_TOP_ORDER.indexOf(o1);
            int i2 = CURRENCY_TOP_ORDER.indexOf(o2);
            if (i1 >= 0 && i2 >= 0) return i1 - i2;
            if (i1 >= 0) return -1;
            if (i2 >= 0) return 1;
            return o1.compareTo(o2);
        });
        return currencyNames.toArray(new String[]{});
    }

    String[] getExchanges(String currency) {
        // only return exchanges that we know about
        List<String> exchangeNames = Exchange.getAllExchangeNames();
        List<String> exchanges = currencyExchange.get(currency);
        if (exchanges == null) return new String[] {};
        exchangeNames.retainAll(exchanges);
        return exchangeNames.toArray(new String[]{});
    }

    Coin getCoin() {
        return coin;
    }

    String getDefaultCurrency() {
        if (currencyExchange.containsKey("USD")) return "USD";
        if (currencyExchange.containsKey("EUR")) return "EUR";
        if (currencyExchange.isEmpty()) return null;
        return currencyExchange.keySet().iterator().next();
    }

    String getDefaultExchange(String currency) {
        List<String> exchanges = currencyExchange.get(currency);
        if (exchanges.contains(Exchange.COINBASE.name())) return Exchange.COINBASE.name();
        return exchanges.get(0);
    }

    String getExchangeCoinName(String exchange, String coin) {
        return obj.getExchangeCoinName(exchange, coin);
    }

    String getExchangeCurrencyName(String exchange, String currency) {
        return obj.getExchangeCurrencyName(exchange, currency);
    }

}
