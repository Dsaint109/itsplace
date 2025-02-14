package itsplace.library.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class DurationFromNow {
	
//	@Autowired 
	//private static  MessageSource messageSource;
	
	/**
	 * 현재부터 "yyyyMMddHHmmss" 포맷의 날짜 차이 레이블
	 * @param date1
	 * @return String
	 */
	public static String getTimeDiffLabel(String date1) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			return getTimeDiffLabel(sdf.parse(date1), new Date());
		} catch (ParseException e) {
			return "-";
		}
	}

	/**
	 * 현재부터 Date 포맷의 날짜 차이 레이블
	 * @param d1
	 * @return String
	 */
	public static String getTimeDiffLabel(Date d1) {
		return getTimeDiffLabel(d1, new Date());
	}

	/**
	 * "yyyyMMddHHmmss" 포맷의 날짜 차이 레이블
	 * @param date1
	 * @param date2
	 * @return String
	 */
	public static String getTimeDiffLabel(String date1, String date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			return getTimeDiffLabel(sdf.parse(date1), sdf.parse(date2));
		} catch (ParseException e) {
			return "-";
		}
	}

	/**
	 * java.util.Date 포맷의 날짜 차이 레이블
	 * @param d1
	 * @param d2
	 * @return String
	 */
	public static String getTimeDiffLabel(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		int sec = (int)(diff / 1000);
		if (sec < 5) return getString("DateLabel.lessthan5seconds");
		if (sec < 60) return getString(sec , "DateLabel.seconds");

		int min = (int)(sec / 60);
		if (min < 60) return getString(min , "DateLabel.minutes");

		int hour = (int)(min / 60);
		if (hour < 24) return getString(hour , "DateLabel.hours");

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = (Calendar) c1.clone();
		c1.setTime(d1);
		c2.setTime(d2);

		int day = c2.get(Calendar.DATE) - c1.get(Calendar.DATE);
		if (day <= 0) {
			day = hour / 24;
		}

		if (hour/24 < 30) {
			if (day == 1) return getString("DateLabel.yesterday");
			if (day == 2) return getString("DateLabel.twodays");
			return getString(day , "DateLabel.days");
		}

		int month = hour / 24 / 30;
		if (month == 1) return getString("DateLabel.onemonth");
		if (month == 2) return getString("DateLabel.twomonths");
		if (month < 12) return getString(month , "DateLabel.months");

		int year = month / 12;
		if (year == 1) return getString("DateLabel.lastyear");
		return getString(year , "DateLabel.years");

	}
	private static final String BUNDLE_NAME = "net.itsplace.util.messages";

	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public static void setLocale(Locale locale) {
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

	/**
	 * add space between number and label according to locale
	 * @param value
	 * @param key
	 * @return DateLabel
	 */
	private static String getString(int value, String key) {
		String space =
			("ko".equals(RESOURCE_BUNDLE.getLocale().getLanguage())) ? "" : " ";
		return value + space + getString(key);
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static  String test2(){
		//return messageSource.getMessage("DateLabel.lastyear", null,Locale.getDefault());
		return "kkkk";
	}
}
