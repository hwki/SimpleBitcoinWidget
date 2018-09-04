package com.brentpanther.bitcoinwidget;

import java.util.ArrayList;
import java.util.List;

public enum Currency {
	
	USD("$#,###.00"),
    EUR("€#,###.00"),
	AUD("$#,###.00"),
    BRL("R$#,###.00"),
	CAD("$#,###.00"),
	CHF("CHF#,###.00"),
    CLP("$#,###.00"),
	CNY("¥#,###.00"),
    CZK("#,###.00 Kč"),
	DKK("#,###.00 kr"),
	GBP("£#,###.00"),
	HKD("$#,###.00"),
    IDR("Rp#,###.00"),
    ILS("₪#,###.00"),
    INR("₹#,###.00"),
	JPY("¥#,###.00"),
    KRW("₩#,###.00"),
    MXN("$#,###.00"),
    MYR("RM#,###.00"),
    NGN("₦#,###.00"),
    NOK("#,###.00 kr"),
	NZD("$#,###.00"),
    PHP("₱#,###.00"),
    PKR("₨#,####.00"),
	PLN("#,###.00 zł"),
    RON("#,###.00 lei"),
	RUB("#,###.00 руб"),
    RUR("#,###.00 руб"),
	SEK("#,###.00 kr"),
	SGD("$#,###.00"),
	THB("฿#,###.00"),
    TRY("₺#,###.00"),
    TWD("$#,###.00"),
    UAH("₴#,###.00"),
    VEF("Bs#,###.00"),
    VND("₫#,###.00"),
    ZAR("R#,###.00");

    private static final List<String> ALL_CURRENCY_NAMES;

    static {
        ALL_CURRENCY_NAMES = new ArrayList<>();
        for (Currency currency : Currency.values()) {
            ALL_CURRENCY_NAMES.add(currency.name());
        }
    }

    public static List<String> getAllCurrencyNames() {
        return new ArrayList<>(ALL_CURRENCY_NAMES);
    }

	String format;

    Currency(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

}
