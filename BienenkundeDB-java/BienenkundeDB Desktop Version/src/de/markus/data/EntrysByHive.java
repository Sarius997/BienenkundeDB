package de.markus.data;

import java.util.ArrayList;

public class EntrysByHive {
	private String hiveName = null;
	private ArrayList<Entry> entrys = new ArrayList<Entry>(5);

	public EntrysByHive(String hiveName, ArrayList<Entry> entrys) {
		this.hiveName = hiveName;
		this.entrys = entrys;
	}

	public void addEntry(Entry data) {
		entrys.add(data);
	}

	public Object[] getEntrys() {
		return entrys.toArray();
	}

	public String getHiveName() {
		return hiveName;
	}
}
