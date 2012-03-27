package com.ipaper.myapp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Time {
	public static Date getCurrentIndiaTime() {
		Calendar cal = Calendar.getInstance();
		//cal.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		//Date today = cal.getTime();
		
		return getDateInTimeZone(cal.getTime(), "Asia/Calcutta");
	}
	
	public static Date addandReturnDate(int days){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, days);
		return getDateInTimeZone(cal.getTime(), "Asia/Calcutta");
	}

	public static void main(String[] args) {
		Date today = Time.getCurrentIndiaTime();		
		Date yesterday = Time.addandReturnDate(-1);
		Date daybeforyesterday = Time.addandReturnDate(-2);

		System.out.println(daybeforyesterday);
	}

	public static Date getDateInTimeZone(Date currentDate, String timeZoneId) {
		TimeZone tz = TimeZone.getTimeZone(timeZoneId);
		Calendar mbCal = new GregorianCalendar(TimeZone.getTimeZone(timeZoneId));
		mbCal.setTimeInMillis(currentDate.getTime());

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

		return cal.getTime();
	}

}
