package com.brentpanther.bitcoinwidget;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.RemoteViews;

/**
 * Different currencies take up different amounts of space on the widget.
 * This class maps currencies to which TextView should display them.
 * Also formats the text of certain currencies which need to be split over 2 lines.
 * @author Panther
 */
public class CurrencySizeMapper {
	
	enum Size {
		LARGE(R.id.bitcoinValue), 
		SMALL(R.id.bitcoinValueSmall), 
		SMALL_SPLIT(R.id.bitcoinValueSmall) {
			@Override
			String loadText(String text) {
				text = text.replaceFirst("\\$", " \\$");
				return text.replace("\u00a0", " ");
			}
		};
		
		int id;

		String loadText(String text) {
			return text;
		}
		
		Size(int id) {
			this.id = id;
		}
	}
	
	@SuppressWarnings("serial")
	private static final Map<String, Size> sizeMap = new HashMap<String, Size>() {{
		put("USD", Size.LARGE);
		put("AUD", Size.SMALL_SPLIT);
		put("CAD", Size.SMALL_SPLIT);
		put("CHF", Size.SMALL_SPLIT);
		put("CNY", Size.SMALL);
		put("DKK", Size.SMALL);
		put("EUR", Size.LARGE);
		put("GBP", Size.LARGE);
		put("HKD", Size.SMALL_SPLIT);
		put("JPY", Size.LARGE);
		put("NZD", Size.SMALL_SPLIT);
		put("PLN", Size.SMALL);
		put("RUB", Size.SMALL_SPLIT);
		put("SEK", Size.LARGE);
		put("SGD", Size.SMALL_SPLIT);
		put("THB", Size.SMALL);
	}};
	
	public static void setText(RemoteViews views, String currency, String text) {
		Size s = sizeMap.get(currency);
		if(text!=null) {
			views.setTextViewText(s.id, s.loadText(text));
		}
		views.setViewVisibility(s.id, View.VISIBLE);
		views.setViewVisibility(R.id.loading, View.GONE);
		views.setViewVisibility(R.id.bitcoinImage, View.VISIBLE);
	}
	
	public static void setLoading(RemoteViews views) {
		views.setViewVisibility(R.id.loading, View.VISIBLE);
		views.setViewVisibility(R.id.bitcoinValue, View.GONE);
		views.setViewVisibility(R.id.bitcoinValueSmall, View.GONE);
		views.setViewVisibility(R.id.bitcoinImage, View.GONE);
	}

}
