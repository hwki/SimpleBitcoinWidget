package com.brentpanther.bitcoinwidget;

public enum Currency {
	
	USD("$#,###.00", "$#,###"),
	AUD("A$#,###.00", "A$#,###.00", "A$#,###"),
	CAD("C$#,###.00", "C$#,###.00", "C$#,###"),
	CHF("#,###.00 Fr", "#,### Fr"),
	CNY("¥#,###.00", "¥#,###"),
	DKK("#,###.00 kr", "#,### kr"),
	EUR("€#,###.00", "€#,###"),
	GBP("£#,###.00", "£#,###"),
	HKD("HK$\n#,###.00", "HK$\n#,###"),
	JPY("¥#,###", "¥#,###"),
	NZD("NZ$\n#,###.00", "NZ$\n#,###"),
	PLN("#,###.00 zł", "#,### zł"),
	RUB("#,###.00 руб", "#,###.00 руб", "#,###\nруб"),
    RUR("#,###.00 руб", "#,###.00 руб", "#,###\nруб"), //same as RUR
	SEK("#,### Kr", "#,###.00 Kr", "#,###.00 Kr"),
	SGD("S$#,###.00", "S$#,###.00", "S$\n#,###"),
	THB("฿#,###.00", "฿#,###"),
    NOK("#,###.00\nKr", "#,### Kr", "#,###\nKr"),
    CZK("#,###.00\nKč", "#,###.00 Kč", "#,###\nKč"),
    BRL("R$#,###.00", "R$#,###.00", "R$#,###"),
    ILS("₪#,###.00", "₪#,###"),
    ZAR("R #,###.00", "R #,###"),
    TRY("#,### TL", "#,### TL"),
    UAH("₴#,###", "₴#,###"),
    MXN("MX$#,###", "MX$#,####"),
    RON("#,### lei", "#,### lei"),
    KRW("₩ #,###", "₩ #,###");

	String format;
    String thousandFormat;
    String tenThousandFormat;

    Currency(String format, String tenThousandFormat) {
        this(format, null, tenThousandFormat);
    }

    Currency(String format, String thousandFormat, String tenThousandFormat) {
        this.format = format;
        this.thousandFormat = thousandFormat;
        this.tenThousandFormat = tenThousandFormat;
    }

    public String getFormat(double amount) {
        if(amount>=10000 && tenThousandFormat != null) return tenThousandFormat;
        if(amount>=1000 && thousandFormat != null) return thousandFormat;
        return format;
    }

}
