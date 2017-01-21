package de.markus.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import de.markus.BienenkundeDB;
import de.markus.PropertiesHandler;

public class EntryHandler {
//	private ArrayList<Entry> entrys = new ArrayList<Entry>(5);

	private ArrayList<EntrysByHive> hives = new ArrayList<EntrysByHive>(5);

	private XStream xstream;
	private File fileToWork;

	private PropertiesHandler propHandler = new PropertiesHandler();

	public EntryHandler() {

		fileToWork = new File(propHandler.getSaveFile());
		try {
			xstream = new XStream(new StaxDriver());
			// nur zu Testzwecken
			// throw new IOException("test", new Throwable("test message"));
		} catch (Exception e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}

		// try {
		// boolean check = fileToWork.exists();
		// fileToWork.createNewFile();
		// if (check == false) {
		// save();
		// }
		// } catch (IOException e) {
		// System.out.println(BienenkundeDB.dateTime());
		// e.printStackTrace();
		// }
		try {
			load();
		} catch (Exception e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}
	}

	public void save() {
		// finally saves the data to separate files for each hive
		// might get changed later on to even use separate files for each entry,
		// but that way, naming of the files has to depend on the system time
		// tag too
		try {
			File saveDir = new File(propHandler.getSaveSubDirectory());
			saveDir.mkdir();

			String[] hiveNames = getHiveNames();

			for (int i = 0; i < hiveNames.length; i++) {
				Writer writer = new FileWriter(propHandler.getSaveSubDirectory() + "/" + hiveNames[i] + ".xml");

				xstream.toXML(getHiveByName(hives, hiveNames[i]), writer);
			}
		} catch (IOException e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}

		// Writer writer = null;
		// try {
		// writer = new FileWriter(fileToWork);
		// } catch (IOException e) {
		// System.out.println(BienenkundeDB.dateTime());
		// e.printStackTrace();
		// }
		// xstream.toXML(entrys, writer);
	}

	@SuppressWarnings("unchecked")
	public void load() {
		if (fileToWork.exists()) {
			ArrayList<Entry> entrys = (ArrayList<Entry>) xstream.fromXML(fileToWork);

			// Compatibility for first release. Do not want to store the entrys
			// in
			// one big file anymore
			int currentVersion = Integer.parseInt(BienenkundeDB.VERSION_NUMBER);
			int lastUsedVersion = Integer.parseInt(propHandler.getLastVersion());

			if (lastUsedVersion < currentVersion) {
				if (lastUsedVersion < 5) {

					for (Iterator<Entry> iterator = entrys.iterator(); iterator.hasNext();) {
						Entry entry = (Entry) iterator.next();
						String entryName = entry.getName().substring(entry.getName().indexOf(", ") + 2);
						EntrysByHive hiveToAddEntryTo = getHiveByName(hives, entryName);
						entry.setVersionNumber(currentVersion);
						hiveToAddEntryTo.addEntry(entry);
					}

					save();

					// the old file is not deleted yet, as I want some way to
					// verify
					// this process first
					fileToWork.delete();
				}
			}
		} else {
			File saveDir = new File(propHandler.getSaveSubDirectory());
			hives = new ArrayList<EntrysByHive>(5);
			
			File[] saveFiles = saveDir.listFiles();
			
			for (int i = 0; i < saveFiles.length; i++) {
				EntrysByHive dat = (EntrysByHive) xstream.fromXML(saveFiles[i]);
				
				hives.add(dat);
			}
			
		}
		
		propHandler.setLastVersionToCurrent();

	}

	public void addEntry(Entry data) {
		EntrysByHive hiv = getHiveByName(hives, data.getName().substring(data.getName().indexOf(", ") + 2));
		hiv.addEntry(data);
		save();
	}

	private String[] getHiveNames() {
		String[] hiveNames = new String[hives.size()];

		for (int i = 0; i < hives.size(); i++) {
			EntrysByHive hive = hives.get(i);

			hiveNames[i] = hive.getHiveName();
		}

		return hiveNames;
	}

	/**
	 * 
	 * @param hives
	 * @param name
	 * @return the hive with the given name if it already exists, or a new hive
	 *         (which is also added to hives) if it did not already exist
	 */
	public EntrysByHive getHiveByName(ArrayList<EntrysByHive> hives, String name) {
		for (Iterator<EntrysByHive> iterator = hives.iterator(); iterator.hasNext();) {
			EntrysByHive entrysByHive = (EntrysByHive) iterator.next();

			if (entrysByHive.getHiveName().equalsIgnoreCase(name))
				return entrysByHive;
		}

		EntrysByHive result = new EntrysByHive(name, new ArrayList<Entry>(5));
		hives.add(result);

		return result;
	}

//	public void deleteAll() {
//		entrys.clear();
//		save();
//	}

	public Object[] getEntrys() {
		ArrayList<Object> entrys = new ArrayList<Object>(5);
		for (Iterator<EntrysByHive> iterator = hives.iterator(); iterator.hasNext();) {
			EntrysByHive tmp = (EntrysByHive) iterator.next();
			
			entrys.addAll(Arrays.asList(tmp.getEntrys()));
		}
		return entrys.toArray();
	}
}
