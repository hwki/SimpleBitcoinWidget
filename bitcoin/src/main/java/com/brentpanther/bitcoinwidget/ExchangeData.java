package com.brentpanther.bitcoinwidget;

import com.google.gson.Gson;

import java.util.ArrayList;
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

        public List<String> loadExchange(String coin) {
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

    ExchangeData(Coin coin, String json) {
        this.coin = coin;
        this.obj = new Gson().fromJson(json, JsonExchangeObject.class);
        currencyExchange = new HashMap<>();
        this.obj.loadCurrencies(coin.name());
    }

    String[] getCurrencies() {
        // only return currencies that we know about
        List<String> currencyNames = Currency.getAllCurrencyNames();
        currencyNames.retainAll(currencyExchange.keySet());
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
