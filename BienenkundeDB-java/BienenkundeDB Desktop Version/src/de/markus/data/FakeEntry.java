package de.markus.data;

import java.util.Calendar;

public class FakeEntry {
	private Object[] data = new Object[25];

	public FakeEntry() {
		Calendar cal = Calendar.getInstance();
		int date = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		String todayStr = String.format("%d." + (month < 10 ? "0" : "") + "%d.%d, ", date, month, year);
		
		data = new Object[]{todayStr,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,"","","",false,"",System.currentTimeMillis()};
	}

	public String getName() {
		return (String) data[0];
	}

	public Object[] getData() {
		return data;
	}
}
