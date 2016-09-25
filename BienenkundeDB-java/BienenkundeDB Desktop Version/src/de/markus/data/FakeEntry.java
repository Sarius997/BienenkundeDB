package de.markus.data;

import java.util.Date;

public class FakeEntry {
	private Object[] data = new Object[25];

	@SuppressWarnings("deprecation")
	public FakeEntry() {
		Date today = new Date(System.currentTimeMillis());
		int date = today.getDate();
		int month = today.getMonth() + 1;
		int year = today.getYear() + 1900;
		String todayStr = "" + date + "." + ((month) < 10 ? "0" + month : month ) + "." + year + ", ";
		
		data = new Object[]{todayStr,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true,"","","",false,"",System.currentTimeMillis()};
	}

	public String getName() {
		return (String) data[0];
	}

	public Object[] getData() {
		return data;
	}
}
