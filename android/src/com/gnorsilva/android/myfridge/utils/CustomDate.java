package com.gnorsilva.android.myfridge.utils;

public class CustomDate implements Comparable<CustomDate> {
	private int year;
	private int month;
	private int day;

	/**
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public CustomDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	/**
	 * @param date should be in the format (yyyy-mm-dd) or (yyyy/mm/dd)
	 */
	public CustomDate(String date){
		String[] values = date.split("\\D");
		this.year = Integer.valueOf(values[0]);
		this.month = Integer.valueOf(values[1]);
		this.day = Integer.valueOf(values[2]);
	}

	public String toString() {
		return year + "-" + month + "-" + day;
	}

	@Override
	public int hashCode() {
		int totalDays = (int) (365.25 * ( year - 1 ));

		if(month>2 && isLeapYear(year)){
				totalDays++;
		}
		
		switch (month) {
		case 2:
			totalDays += 31;
			break;
		case 3:
			totalDays += 59;
			break;
		case 4:
			totalDays += 90;
			break;
		case 5:
			totalDays += 120;
			break;
		case 6:
			totalDays += 151;
			break;
		case 7:
			totalDays += 181;
			break;
		case 8:
			totalDays += 212;
			break;
		case 9:
			totalDays += 243;
			break;
		case 10:
			totalDays += 273;
			break;
		case 11:
			totalDays += 304;
			break;
		case 12:
			totalDays += 334;
			break;
		}

		totalDays += day;

		return totalDays;
	}

	public static boolean isLeapYear(int year) {
		boolean mod4 = (year % 4 == 0);
		boolean mod100 = (year % 100 == 0);
		boolean mod400 = (year % 400 == 0);

		return (mod4) && !((mod100) && !(mod400));
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof CustomDate ? this.hashCode() == o.hashCode() : false;
	}
	
	@Override
	public int compareTo(CustomDate otherDate) {
		return this.hashCode()-otherDate.hashCode();
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
}