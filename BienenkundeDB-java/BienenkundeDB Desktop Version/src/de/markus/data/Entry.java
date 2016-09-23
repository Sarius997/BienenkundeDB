package de.markus.data;

import de.markus.BienenkundeDB;

public class Entry {
	private Object[] data = new Object[25];

	public Entry(Object[] newData) {
		if (newData.length == data.length) {
			for (int i = 0; i < newData.length; i++) {
				data[i] = newData[i];
			}
		} else {
			System.err
					.println(BienenkundeDB.dateTime() + "Ein Fehler beim Übertragen der Daten wurde festgestellt!!"
							+ "Bitte senden Sie einen Fehlerbericht an die angegebene E-mail Adresse");
		}
	}

	public String getName() {
		return (String) data[0];
	}

	public Object[] getData() {
		return data;
	}
}
