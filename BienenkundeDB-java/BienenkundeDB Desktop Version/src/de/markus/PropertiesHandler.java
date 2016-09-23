package de.markus;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesHandler {

	private final String ResourcePath = System.getProperty("user.home") + "/AppData/Roaming/BienenkundeDB/";
	private final String IconName = "icon.jpg";
	private final String BackgroundName = "background.jpg";
	private final String SaveFileName = "saveData.xml";
	private final String SettingsFileName = "settings.prop";

	private File outputFile = new File(ResourcePath + "/Logging/log " + BienenkundeDB.date() + ".txt");
	private File settingsFile = new File(ResourcePath + SettingsFileName);

	private Properties props = new Properties();

	private static final String propMainWndX = "mainWndPosX";
	private static final String propMainWndY = "mainWndPosY";
	private static final String propInfoWndX = "infoWndPosX";
	private static final String propInfoWndY = "infoWndPosY";
	private static final String propLoadWndX = "loadWndPosX";
	private static final String propLoadWndY = "loadWndPosY";

	@SuppressWarnings("unused")
	private static final String propMainWndWdth = "mainWndWdth";
	private static final String propMainWndHght = "mainWndHght";
	private static final String propLoadWndWdth = "loadWndWdth";
	private static final String propLoadWndHght = "loadWndHght";

	public PropertiesHandler() {
		File dataPath = new File(ResourcePath);
		dataPath.mkdir();
		try {
			outputFile.createNewFile();
			settingsFile.createNewFile();
			props.load(new FileInputStream(settingsFile));
		} catch (IOException e) {
			System.out.println(BienenkundeDB.dateTime());
			e.printStackTrace();
		}
	}

	public URL getIcon() {
		return PropertiesHandler.class.getResource("/images/" + IconName);
	}

	public URL getBackground() {
		return PropertiesHandler.class.getResource("/images/" + BackgroundName);
	}

	public String getSaveFile() {
		return ResourcePath + SaveFileName;
	}

	public String getSettingsFile() {
		return ResourcePath + SettingsFileName;
	}

	public String getResourcePath() {
		return ResourcePath;
	}

	public void setMainWndPos(Point p) {
		try {
			props.load(new FileInputStream(settingsFile));
			props.setProperty(propMainWndX, "" + p.x);
			props.setProperty(propMainWndY, "" + p.y);
			props.store(new FileOutputStream(settingsFile), "Please do not modify this");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setInfoWndPos(Point p) {
		try {
			props.load(new FileInputStream(settingsFile));
			props.setProperty(propInfoWndX, "" + p.x);
			props.setProperty(propInfoWndY, "" + p.y);
			props.store(new FileOutputStream(settingsFile), "Please do not modify this");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLoadWndPos(Point p) {
		try {
			props.load(new FileInputStream(settingsFile));
			props.setProperty(propLoadWndX, "" + p.x);
			props.setProperty(propLoadWndY, "" + p.y);
			props.store(new FileOutputStream(settingsFile), "Please do not modify this");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Point getMainWndPos() {
		try {
			props.load(new FileInputStream(settingsFile));
			return new Point(Integer.parseInt(props.getProperty(propMainWndX, "0")),
					Integer.parseInt(props.getProperty(propMainWndY, "0")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Point(0, 0);
	}

	public Point getInfoWndPos() {
		try {
			props.load(new FileInputStream(settingsFile));
			Point p = getMainWndPos();
			// return new Point(Integer.parseInt(props.getProperty(propInfoWndX,
			// "0")), Integer.parseInt(props.getProperty(propInfoWndY, "0")));
			return new Point(Integer.parseInt(props.getProperty(propInfoWndX, "" + (p.x + 10))),
					Integer.parseInt(props.getProperty(propInfoWndY, "" + (p.y + 10))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Point(0, 0);
	}

	public Point getLoadWndPos() {
		try {
			props.load(new FileInputStream(settingsFile));
			Point p = getMainWndPos();
			// return new Point(Integer.parseInt(props.getProperty(propLoadWndX,
			// "0")), Integer.parseInt(props.getProperty(propLoadWndY, "0")));
			return new Point(Integer.parseInt(props.getProperty(propLoadWndX, "" + (p.x + 10))),
					Integer.parseInt(props.getProperty(propLoadWndY, "" + (p.y + 10))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Point(0, 0);
	}

	public void setMainWndSize(int width, int height) {
		try {
			props.load(new FileInputStream(settingsFile));
//			props.setProperty(propMainWndWdth, "" + width);
			props.setProperty(propMainWndHght, "" + height);
			props.store(new FileOutputStream(settingsFile), "Please do not modify this");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLoadWndSize(int width, int height) {
		try {
			props.load(new FileInputStream(settingsFile));
			props.setProperty(propLoadWndWdth, "" + width);
			props.setProperty(propLoadWndHght, "" + height);
			props.store(new FileOutputStream(settingsFile), "Please do not modify this");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getMainWndHght() {
		try {
			props.load(new FileInputStream(settingsFile));
			return Integer.parseInt(props.getProperty(propMainWndHght, "700"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 700;
	}

	public int getMainWndWdth() {
//		try {
//			props.load(new FileInputStream(settingsFile));
//			return Integer.parseInt(props.getProperty(propMainWndWdth, "500"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return 500;
	}

	public int getLoadWndHght() {
		try {
			props.load(new FileInputStream(settingsFile));
			return Integer.parseInt(props.getProperty(propLoadWndHght, "500"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 500;
	}

	public int getLoadWndWdth() {
		try {
			props.load(new FileInputStream(settingsFile));
			return Integer.parseInt(props.getProperty(propLoadWndWdth, "300"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 300;
	}
}
