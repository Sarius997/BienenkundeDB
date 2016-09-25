package de.markus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;

import de.markus.gui.startWindow;
import de.markus.updates.Updater;

public class BienenkundeDB {
	public static final String VERSION = "1.0a";
	public static final String VERSION_NUMBER = "3";
	// private static PropertiesHandler propHandler = new PropertiesHandler();
	public static startWindow startWindow;
	
	public static void main(String[] args) {
		if(args != null && args.length != 0){
			if(args.length == 1){
				switch (args[0]) {
				case "getVersionInfo":
					System.out.print(getVersionInfo());
					break;
				case "launchDefault":
					System.out.println("Launching BienenkundeDB. Further output is printed to the log files!");
					launchBienenkundeDB();
					break;
				default:
					System.out.println("Invalid Arguments!");
					break;
				}
			} else {
				System.out.println("Invalid Arguments!");
			}
		} else {
			System.out.println("Launching BienenkundeDB. Further output is printed to the log files!");
			launchBienenkundeDB();
		}
		Updater updater = new Updater();
		updater.checkForUpdates(false);
	}

	public static void launchBienenkundeDB() {
		File outputDir = new File(System.getProperty("user.home") + "/AppData/Roaming/BienenkundeDB/Logging/");
		File outputFile = new File(outputDir + "/log " + date() + ".txt");
		try {
			outputDir.mkdir();
			outputFile.createNewFile();
			System.setOut(new PrintStream(new FileOutputStream(outputFile, true)));
			System.setErr(new PrintStream(new FileOutputStream(outputFile, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println(dateTime() + "Initializing app :)");
		System.out.println(dateTime() + "Logging initialized");
		// propHandler = new PropertiesHandler();
		startWindow = new startWindow();
		setVisible(startWindow.window, true);
		System.out.println(dateTime() + "The window is now visible");
	}

	public static void setVisible(JFrame window, boolean visible) {
		if (window != null) {
			window.setVisible(visible);
			System.out.println("Inner Panel Size: x=" + window.getContentPane().getWidth() + " y=" + window.getContentPane().getHeight());
		}
	}

	public static String dateTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		String dateString = dateFormat.format(date);

		return dateString + " :: ";
	}
	
	public static String date(){
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		
		return dateString;
	}
	
	private static String getVersionInfo(){
		return "name=" + VERSION + " number=" + VERSION_NUMBER;
	}
}
