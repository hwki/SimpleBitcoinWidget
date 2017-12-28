package com.brentpanther.bitcoinwidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ExchangeData {

    private final Coin coin;
    private final Map<String, List<String>> CURRENCY_TO_EXCHANGE = new HashMap<>();

    ExchangeData(Coin coin, String json) throws JSONException {
        this.coin = coin;
        JSONObject obj = new JSONObject(json);
        JSONArray exchanges = obj.getJSONArray("exchanges");
        for (int i = 0; i < exchanges.length(); i++) {
            addExchange(coin.name(), exchanges.getJSONObject(i));
        }
    }

    private void addExchange(String coinName, JSONObject exchange) throws JSONException {
        JSONArray coins = exchange.getJSONArray("coins");
        for (int i = 0; i < coins.length(); i++) {
            JSONObject coin = coins.getJSONObject(i);
            if (!coin.getString("name").equals(coinName)) continue;
            String exchangeName = exchange.getString("name");
            JSONArray currencies = coin.getJSONArray("currencies");
            for (int j = 0; j < currencies.length(); j++) {
                addCurrency(exchangeName, currencies.get(j).toString());
            }
        }
    }

    private void addCurrency(String exchangeName, String currency) {
        if (!CURRENCY_TO_EXCHANGE.containsKey(currency)) {
            CURRENCY_TO_EXCHANGE.put(currency, new ArrayList<String>());
        }
        CURRENCY_TO_EXCHANGE.get(currency).add(exchangeName);
    }

    String[] getCurrencies() {
        // only return currencies that we know about
        List<String> currencyNames = Currency.getAllCurrencyNames();
        currencyNames.retainAll(CURRENCY_TO_EXCHANGE.keySet());
        return currencyNames.toArray(new String[]{});
    }

    String[] getExchanges(String currency) {
        // only return exchanges that we know about
        List<String> exchangeNames = Exchange.getAllExchangeNames();
        exchangeNames.retainAll(CURRENCY_TO_EXCHANGE.get(currency));
        return exchangeNames.toArray(new String[]{});
    }

    Coin getCoin() {
        return coin;
    }

    String getDefaultCurrency() {
        if (CURRENCY_TO_EXCHANGE.containsKey("USD")) return "USD";
        if (CURRENCY_TO_EXCHANGE.containsKey("EUR")) return "EUR";
        if (CURRENCY_TO_EXCHANGE.isEmpty()) return null;
        return CURRENCY_TO_EXCHANGE.keySet().iterator().next();
    }

    String getDefaultExchange(String currency) {
        List<String> exchanges = CURRENCY_TO_EXCHANGE.get(currency);
        if (exchanges.contains(Exchange.COINBASE.name())) return Exchange.COINBASE.name();
        return exchanges.get(0);
    }
}
