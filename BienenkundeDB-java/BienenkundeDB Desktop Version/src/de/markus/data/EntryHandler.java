package de.markus.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import de.markus.BienenkundeDB;
import de.markus.PropertiesHandler;

public class EntryHandler {
	private ArrayList<Entry> entrys = new ArrayList<Entry>(5);
	private XStream xstream;
	private File fileToWork;

	public EntryHandler() {

		PropertiesHandler propHandler = new PropertiesHandler();

		fileToWork = new File(propHandler.getSaveFile());
		try {
			xstream = new XStream(new StaxDriver());
			// nur zu Testzwecken
			// throw new IOException("test", new Throwable("test message"));
		} catch (Exception e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}

		try {
			boolean check = new File(propHandler.getSaveFile()).exists();
			fileToWork.createNewFile();
			if (check == false) {
				save();
			}
		} catch (IOException e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}
		try {
			load();
		} catch (Exception e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}
	}

	public void save() {
		Writer writer = null;
		try {
			writer = new FileWriter(fileToWork);
		} catch (IOException e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}
		xstream.toXML(entrys, writer);
	}

	@SuppressWarnings("unchecked")
	public void load() {
		entrys = (ArrayList<Entry>) xstream.fromXML(fileToWork);
	}

	public void addEntry(Entry data) {
		entrys.add(data);
		save();
	}

	public void deleteAll() {
		entrys.clear();
		save();
	}

	public Object[] getEntrys() {
		return entrys.toArray();
	}
}
