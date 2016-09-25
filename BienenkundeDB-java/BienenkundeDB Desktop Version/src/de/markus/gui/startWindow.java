package de.markus.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.markus.BienenkundeDB;
import de.markus.PropertiesHandler;
import de.markus.data.Entry;
import de.markus.data.EntryHandler;
import de.markus.data.FakeEntry;
import de.markus.updates.Updater;

public class startWindow {
	
	public Updater updater = new Updater();

	public EntryHandler handler = new EntryHandler();
	public PropertiesHandler propHandler = new PropertiesHandler();

	public JFrame window = new JFrame("BienenkundeDB");
	
	private JBackgroundPanel windowPanel;
	private JScrollPane scrollPane;

	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu = new JMenu("Datei");
	private JMenu menuHelp = new JMenu("Hilfe");

	private JTextField datumNameText = new JTextField();
	private JCheckBox ruhigCB = new JCheckBox("ruhig");
	private JCheckBox geschaeftigCB = new JCheckBox("geschäftig");
	private JCheckBox gereiztCB = new JCheckBox("gereizt");
	private JCheckBox aggressivCB = new JCheckBox("aggressiv");
	private JCheckBox honigOffenCB = new JCheckBox("offener Honig");
	private JCheckBox honigVerdeckeltCB = new JCheckBox("verdeckelter Honig");
	private JCheckBox pollenImStockCB = new JCheckBox("Pollen im Stock");
	private JCheckBox pollenEintragenCB = new JCheckBox("tragen Pollen ein");
	private JCheckBox verkrueppelteBienenCB = new JCheckBox("verkrüppelte Bienen");
	private JCheckBox drohnenCB = new JCheckBox("Drohnen");
	private JCheckBox brutMaennlichCB = new JCheckBox("Brut männlich");
	private JCheckBox brutWeiblichCB = new JCheckBox("Brut weiblich");
	private JCheckBox eierCB = new JCheckBox("Eier");
	private JCheckBox schwarmzellenCB = new JCheckBox("Schwarmzellen");
	private JCheckBox nachschaffungszellenCB = new JCheckBox("Nachschaffungszellen");
	private JRadioButton koenigigJaRad = new JRadioButton("ja");
	private JRadioButton koeniginNeinRad = new JRadioButton("nein");
	private JRadioButton koeniginVielleichtRad = new JRadioButton("vielleicht ;)");
	private JTextField koeniginJahrText = new JTextField();
	private JTextField raehmchenZahlText = new JTextField();
	private JTextField raehmchenErweitertText = new JTextField();
	private JCheckBox leereWabenCB = new JCheckBox("leere Waben");
	private JTextField sonstigeWabenText = new JTextField();

	private static int returnValue = -1;
	private static int iterations;

	private static JLabel neededForFont = new JLabel("test");
	private Object[] savedEntrys = handler.getEntrys();
	private JButton[] buttonsToSelect = new JButton[savedEntrys.length];

	private JDialog infoDialog = new JDialog();
	private JDialog windowSelectEntry = new JDialog();

	private final int FONT_SIZE = 12;
	private final int INFO_FONT_SIZE = 18;

	private AbstractAction acInfo;
	private AbstractAction acExit;
	private AbstractAction acSave;
	private AbstractAction acLoad;
	private AbstractAction acReset;
	private AbstractAction acUpdateCheck;

	private ButtonGroup radioGroup = new ButtonGroup();

	@SuppressWarnings("deprecation")
	public startWindow() {
		Date today = new Date(System.currentTimeMillis());
		int date = today.getDate();
		int month = today.getMonth() + 1;
		int year = today.getYear() + 1900;
		String todayStr = "" + date + "." + ((month) < 10 ? "0" + month : month) + "." + year + ", ";
		datumNameText.setText(todayStr);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);

		ImageIcon imageIcon = new ImageIcon(propHandler.getBackground());
		Image image = imageIcon.getImage();
		Image newImage = image.getScaledInstance(500, 700, Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newImage);

		window.setIconImage(new ImageIcon(propHandler.getIcon()).getImage());

		Point p = propHandler.getMainWndPos();
		window.setBounds(p.x, p.y, propHandler.getMainWndWdth(), propHandler.getMainWndHght());
		
		windowPanel = new JBackgroundPanel();
		windowPanel.setLayout(new GridLayout(0, 2, 0, 0));
		windowPanel.setPreferredSize(new Dimension(480, 639));

		addPanelComponents();
		
		scrollPane = new JScrollPane(windowPanel);
		window.add(scrollPane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		addActionListeners();

		menu.add(acSave);
		menu.add(acLoad);
		menu.addSeparator();
		menu.add(acReset);
		menu.addSeparator();
		menu.add(acExit);
		menuBar.add(menu);
		
		menuHelp.add(acUpdateCheck);
		menuHelp.addSeparator();
		menuHelp.add(acInfo);
		menuBar.add(menuHelp);

		window.setJMenuBar(menuBar);

		setTransparent();
		
		resetFields();
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private void showInfo() {
		infoDialog = new JDialog(window, "Info", true);
		JLabel infoText_1 = new JLabel("Bei Problemen oder Anregungen", SwingConstants.CENTER);
		JLabel infoText_2 = new JLabel("erreichbar unter:", SwingConstants.CENTER);
		JButton emailButton = new JButton("markus.hofmann97@gmx.de");
		JButton back = new JButton("OK");
		JLabel version = new JLabel("Version: " + BienenkundeDB.VERSION, SwingConstants.CENTER);
		JButton savePathButton = new JButton("Speicherort öffnen");

		ImageIcon imageIcon = new ImageIcon(propHandler.getBackground());
		Image image = imageIcon.getImage();
		Image newImage = image.getScaledInstance(300, 500, Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newImage);
		infoDialog.setContentPane(new JLabel(imageIcon));

		infoDialog.setIconImage(new ImageIcon(propHandler.getIcon()).getImage());
		Point p = propHandler.getInfoWndPos();
		infoDialog.setBounds(p.x, p.y, 300, 350);
		infoDialog.setLayout(new GridLayout(0, 1));
		infoDialog.add(infoText_1);
		infoDialog.add(infoText_2);
		infoDialog.add(emailButton);
		infoDialog.add(new JLabel());
		infoDialog.add(version);
		infoDialog.add(savePathButton);
		infoDialog.add(new JLabel());
		infoDialog.add(new JLabel());
		infoDialog.add(back);

		back.setOpaque(false);
		back.setContentAreaFilled(false);
		back.setBorderPainted(false);
		emailButton.setOpaque(false);
		emailButton.setContentAreaFilled(false);
		emailButton.setBorderPainted(false);
		savePathButton.setOpaque(false);
		savePathButton.setOpaque(false);
		savePathButton.setContentAreaFilled(false);
		savePathButton.setBorderPainted(false);

		version.setFont(new Font(version.getFont().getFamily(), Font.PLAIN, INFO_FONT_SIZE));
		savePathButton.setFont(new Font(savePathButton.getFont().getFamily(), Font.PLAIN, INFO_FONT_SIZE));
		infoText_1.setFont(new Font(infoText_1.getFont().getFamily(), Font.PLAIN, INFO_FONT_SIZE));
		infoText_2.setFont(new Font(infoText_2.getFont().getFamily(), Font.PLAIN, INFO_FONT_SIZE));
		emailButton.setFont(new Font(emailButton.getFont().getFamily(), Font.PLAIN, INFO_FONT_SIZE));
		back.setFont(new Font(back.getFont().getFamily(), Font.PLAIN, INFO_FONT_SIZE));
		emailButton.setForeground(new Color(0, 0, 60));
		savePathButton.setForeground(new Color(0, 0, 60));
		Font fontEmailButton = emailButton.getFont();
		Map attributesEmailButton = fontEmailButton.getAttributes();
		attributesEmailButton.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		emailButton.setFont(fontEmailButton.deriveFont(attributesEmailButton));

		Font fontSavePathButton = savePathButton.getFont();
		Map attributesSavePathButton = fontSavePathButton.getAttributes();
		attributesSavePathButton.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		savePathButton.setFont(fontSavePathButton.deriveFont(attributesSavePathButton));

		infoDialog.setResizable(false);

		back.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				infoDialog.dispose();
			}
		});
		emailButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(BienenkundeDB.dateTime() + "Trying to send an email");
				String uriStr = "mailto:markus.hofmann97@gmx.de?subject=BienenkundeDB%20desktop%20version:%20"
						+ BienenkundeDB.VERSION + "%20nr:%20" + BienenkundeDB.VERSION_NUMBER;
				try {
					Desktop.getDesktop().browse(new URI(uriStr));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		savePathButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println(BienenkundeDB.dateTime() + "Trying to open the save folder");
					Desktop.getDesktop().open(new File(propHandler.getResourcePath()));
				} catch (IOException e1) {
					System.out.println(BienenkundeDB.dateTime());
					e1.printStackTrace();
				}
			}
		});
		
		infoDialog.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e){
				propHandler.setInfoWndPos(e.getComponent().getLocation());
			}
		});

		infoDialog.show();
	}

	@SuppressWarnings("serial")
	private void addActionListeners() {
		acReset = new AbstractAction("Reset") {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(BienenkundeDB.dateTime() + "Resetting the input fields...");
				resetFields();
				System.out.println(BienenkundeDB.dateTime() + "Resetting done");
			}
		};
		acInfo = new AbstractAction("Info") {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(BienenkundeDB.dateTime() + "Showing the info window");
				showInfo();
			}
		};
		acExit = new AbstractAction("Beenden") {

			@Override
			public void actionPerformed(ActionEvent e) {
				BienenkundeDB.setVisible(BienenkundeDB.startWindow.window, false);
				System.out.println(BienenkundeDB.dateTime() + "Shutting down the app :(");
				System.exit(0);
			}
		};
		acSave = new AbstractAction("Speichern") {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(BienenkundeDB.dateTime() + "Saving the entry...");
				saveThisEntry();
				System.out.println(BienenkundeDB.dateTime() + "Saving done. Resetting fields...");
				resetFields();
				System.out.println(BienenkundeDB.dateTime() + "Resetting fields done");
			}

		};
		acLoad = new AbstractAction("Eintrag Laden") {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(BienenkundeDB.dateTime() + "Attempting to load data");
				entryList();
			}
		};
		acUpdateCheck = new AbstractAction("Nach Updates Suchen") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(BienenkundeDB.dateTime() + "Attempting to check for updates");
				updater.resetLatestBdbVersion();
				updater.resetLatestLauncherVersion();
				updater.checkForUpdates(true);
			}
		};
		
		window.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e){
				propHandler.setMainWndPos(e.getComponent().getLocation());
			}
			
			public void componentResized(ComponentEvent e){
				propHandler.setMainWndSize(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
		});
	}

	private void saveThisEntry() {
		savedEntrys = handler.getEntrys();

		// boolean bereitsVorhanden = false;
		String datumName = datumNameText.getText();
		System.out.println(BienenkundeDB.dateTime() + "trying to save an Entry called: \"" + datumName + "\"");
		// for (int i = 0; i < savedEntrys.length; i++) {
		// Entry entryToCheck = (Entry) savedEntrys[i];
		// Object[] check = entryToCheck.getData();
		// if (((String) check[0]).equals(datumName)) {
		// bereitsVorhanden = true;
		// }
		// }
		// if (bereitsVorhanden == false) {
		boolean ruhig = ruhigCB.isSelected();
		boolean geschaeftig = geschaeftigCB.isSelected();
		boolean gereizt = gereiztCB.isSelected();
		boolean aggressiv = aggressivCB.isSelected();
		boolean honigOffen = honigOffenCB.isSelected();
		boolean honigVerdeckelt = honigVerdeckeltCB.isSelected();
		boolean pollenImStock = pollenImStockCB.isSelected();
		boolean pollenEintragen = pollenEintragenCB.isSelected();
		boolean verkrueppelteBienen = verkrueppelteBienenCB.isSelected();
		boolean drohnen = drohnenCB.isSelected();
		boolean brutMaennlich = brutMaennlichCB.isSelected();
		boolean brutWeiblich = brutWeiblichCB.isSelected();
		boolean eier = eierCB.isSelected();
		boolean schwarmzellen = schwarmzellenCB.isSelected();
		boolean nachschaffungszellen = nachschaffungszellenCB.isSelected();
		boolean koeniginJa = koenigigJaRad.isSelected();
		boolean koeniginNein = koeniginNeinRad.isSelected();
		boolean koeniginVielleicht = koeniginVielleichtRad.isSelected();
		String koeniginJahr = koeniginJahrText.getText();
		String raehmchenZahl = raehmchenZahlText.getText();
		String raehmchenErweitert = raehmchenErweitertText.getText();
		boolean leereWaben = leereWabenCB.isSelected();
		String sonstigeWaben = sonstigeWabenText.getText();

		long time = System.currentTimeMillis();

		Object[] saveEntry = { datumName, ruhig, geschaeftig, gereizt, aggressiv, honigOffen, honigVerdeckelt,
				pollenImStock, pollenEintragen, verkrueppelteBienen, drohnen, brutMaennlich, brutWeiblich, eier,
				schwarmzellen, nachschaffungszellen, koeniginJa, koeniginNein, koeniginVielleicht, koeniginJahr,
				raehmchenZahl, raehmchenErweitert, leereWaben, sonstigeWaben, time };
		handler.addEntry(new Entry(saveEntry));

		System.out.println(BienenkundeDB.dateTime() + "Entry \"" + datumName + "\" saved with the following timestamp: " + time);
		// }
	}

	private void addPanelComponents() {
		radioGroup.add(koenigigJaRad);
		radioGroup.add(koeniginNeinRad);
		radioGroup.add(koeniginVielleichtRad);

		JLabel datumVolk = new JLabel("Datum, Volk");
		datumVolk.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(datumVolk);
		windowPanel.add(datumNameText);

		JLabel verhalten = new JLabel("Verhalten:");
		verhalten.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(verhalten);
		windowPanel.add(ruhigCB);
		windowPanel.add(new JLabel());
		windowPanel.add(geschaeftigCB);
		windowPanel.add(new JLabel());
		windowPanel.add(gereiztCB);
		windowPanel.add(new JLabel());
		windowPanel.add(aggressivCB);

		JLabel gesehen = new JLabel("Gesehen:");
		gesehen.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(gesehen);
		windowPanel.add(new JLabel());
		windowPanel.add(new JLabel());
		windowPanel.add(honigOffenCB);
		windowPanel.add(new JLabel());
		windowPanel.add(honigVerdeckeltCB);
		windowPanel.add(new JLabel());
		windowPanel.add(pollenImStockCB);
		windowPanel.add(new JLabel());
		windowPanel.add(pollenEintragenCB);
		windowPanel.add(new JLabel());
		windowPanel.add(verkrueppelteBienenCB);
		windowPanel.add(new JLabel());
		windowPanel.add(drohnenCB);
		windowPanel.add(new JLabel());
		windowPanel.add(brutMaennlichCB);
		windowPanel.add(new JLabel());
		windowPanel.add(brutWeiblichCB);
		windowPanel.add(new JLabel());
		windowPanel.add(eierCB);
		windowPanel.add(new JLabel());
		windowPanel.add(schwarmzellenCB);
		windowPanel.add(new JLabel());
		windowPanel.add(nachschaffungszellenCB);

		JLabel koenigin = new JLabel("Königin:");
		koenigin.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(koenigin);
		windowPanel.add(new JLabel());
		windowPanel.add(new JLabel());
		windowPanel.add(koenigigJaRad);
		windowPanel.add(new JLabel());
		windowPanel.add(koeniginNeinRad);
		windowPanel.add(new JLabel());
		windowPanel.add(koeniginVielleichtRad);
		JLabel jahr = new JLabel("Jahr");
		jahr.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(jahr);
		windowPanel.add(koeniginJahrText);
		JLabel raehmchenZahl = new JLabel("Rähmchen Anzahl");
		raehmchenZahl.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(raehmchenZahl);
		windowPanel.add(raehmchenZahlText);
		JLabel raehmchenErweitert = new JLabel("erweitert um");
		raehmchenErweitert.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(raehmchenErweitert);
		windowPanel.add(raehmchenErweitertText);
		windowPanel.add(new JLabel());
		windowPanel.add(leereWabenCB);
		JLabel andereWaben = new JLabel("Andere Waben:");
		andereWaben.setFont(new Font(neededForFont.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		windowPanel.add(andereWaben);
		windowPanel.add(sonstigeWabenText);
	}

	private void resetFields() {
		Object[] entryToLoad = new FakeEntry().getData();

		datumNameText.setText((String) entryToLoad[0]);
		ruhigCB.setSelected((boolean) entryToLoad[1]);
		geschaeftigCB.setSelected((boolean) entryToLoad[2]);
		gereiztCB.setSelected((boolean) entryToLoad[3]);
		aggressivCB.setSelected((boolean) entryToLoad[4]);
		honigOffenCB.setSelected((boolean) entryToLoad[5]);
		honigVerdeckeltCB.setSelected((boolean) entryToLoad[6]);
		pollenImStockCB.setSelected((boolean) entryToLoad[7]);
		pollenEintragenCB.setSelected((boolean) entryToLoad[8]);
		verkrueppelteBienenCB.setSelected((boolean) entryToLoad[9]);
		drohnenCB.setSelected((boolean) entryToLoad[10]);
		brutMaennlichCB.setSelected((boolean) entryToLoad[11]);
		brutWeiblichCB.setSelected((boolean) entryToLoad[12]);
		eierCB.setSelected((boolean) entryToLoad[13]);
		schwarmzellenCB.setSelected((boolean) entryToLoad[14]);
		nachschaffungszellenCB.setSelected((boolean) entryToLoad[15]);
		koenigigJaRad.setSelected((boolean) entryToLoad[16]);
		koeniginNeinRad.setSelected((boolean) entryToLoad[17]);
		koeniginVielleichtRad.setSelected((boolean) entryToLoad[18]);
		koeniginJahrText.setText((String) entryToLoad[19]);
		raehmchenZahlText.setText((String) entryToLoad[20]);
		raehmchenErweitertText.setText((String) entryToLoad[21]);
		leereWabenCB.setSelected((boolean) entryToLoad[22]);
		sonstigeWabenText.setText((String) entryToLoad[23]);
	}

	@SuppressWarnings("deprecation")
	private void entryList() {
		windowSelectEntry = new JDialog(window, "Eintrag laden", true);
		windowSelectEntry.setResizable(true);

		savedEntrys = handler.getEntrys();
		buttonsToSelect = new JButton[savedEntrys.length];
		Point p = propHandler.getLoadWndPos();
		windowSelectEntry.setBounds(p.x, p.y, propHandler.getLoadWndWdth(), propHandler.getLoadWndHght());
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(savedEntrys.length, 1, 0, 0));

		returnValue = -1;

		for (int i = 0; i < savedEntrys.length; i++) {
			String name = ((Entry) savedEntrys[i]).getName();
			buttonsToSelect[i] = new JButton(name);
			buttonsToSelect[i].setSize(buttonsToSelect[i].getWidth(), 20);
			buttonsToSelect[i].setPreferredSize(new Dimension(buttonsToSelect[i].getWidth(), 40));
		}
		for (int i = 0; i < buttonsToSelect.length; i++) {
			panel.add(buttonsToSelect[i]);
		}

		JScrollPane scrollPane = new JScrollPane(panel);
		windowSelectEntry.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		for (iterations = 0; iterations < buttonsToSelect.length; iterations++) {
			buttonsToSelect[iterations].addActionListener(new ActionListener() {
				int i = iterations;

				@Override
				public void actionPerformed(ActionEvent arg0) {

					returnValue = i;

					windowSelectEntry.dispose();

					savedEntrys = handler.getEntrys();
					Entry entryToDisplay = (Entry) savedEntrys[returnValue];
					Object[] entryToLoad = entryToDisplay.getData();

					datumNameText.setText((String) entryToLoad[0]);
					ruhigCB.setSelected((boolean) entryToLoad[1]);
					geschaeftigCB.setSelected((boolean) entryToLoad[2]);
					gereiztCB.setSelected((boolean) entryToLoad[3]);
					aggressivCB.setSelected((boolean) entryToLoad[4]);
					honigOffenCB.setSelected((boolean) entryToLoad[5]);
					honigVerdeckeltCB.setSelected((boolean) entryToLoad[6]);
					pollenImStockCB.setSelected((boolean) entryToLoad[7]);
					pollenEintragenCB.setSelected((boolean) entryToLoad[8]);
					verkrueppelteBienenCB.setSelected((boolean) entryToLoad[9]);
					drohnenCB.setSelected((boolean) entryToLoad[10]);
					brutMaennlichCB.setSelected((boolean) entryToLoad[11]);
					brutWeiblichCB.setSelected((boolean) entryToLoad[12]);
					eierCB.setSelected((boolean) entryToLoad[13]);
					schwarmzellenCB.setSelected((boolean) entryToLoad[14]);
					nachschaffungszellenCB.setSelected((boolean) entryToLoad[15]);
					koenigigJaRad.setSelected((boolean) entryToLoad[16]);
					koeniginNeinRad.setSelected((boolean) entryToLoad[17]);
					koeniginVielleichtRad.setSelected((boolean) entryToLoad[18]);
					koeniginJahrText.setText((String) entryToLoad[19]);
					raehmchenZahlText.setText((String) entryToLoad[20]);
					raehmchenErweitertText.setText((String) entryToLoad[21]);
					leereWabenCB.setSelected((boolean) entryToLoad[22]);
					sonstigeWabenText.setText((String) entryToLoad[23]);

					System.out.println(BienenkundeDB.dateTime() + "Loading data with name: \"" + entryToLoad[0]
							+ "\" and timestamp: " + entryToLoad[24]);
				}
			});
		}
		
		windowSelectEntry.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e){
				propHandler.setLoadWndPos(e.getComponent().getLocation());
			}
			
			public void componentResized(ComponentEvent e){
				propHandler.setLoadWndSize(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
		});

		windowSelectEntry.show();

	}

	private void setTransparent() {

		ruhigCB.setContentAreaFilled(false);
		geschaeftigCB.setContentAreaFilled(false);
		gereiztCB.setContentAreaFilled(false);
		aggressivCB.setContentAreaFilled(false);
		honigOffenCB.setContentAreaFilled(false);
		honigVerdeckeltCB.setContentAreaFilled(false);
		pollenImStockCB.setContentAreaFilled(false);
		pollenEintragenCB.setContentAreaFilled(false);
		verkrueppelteBienenCB.setContentAreaFilled(false);
		drohnenCB.setContentAreaFilled(false);
		brutMaennlichCB.setContentAreaFilled(false);
		brutWeiblichCB.setContentAreaFilled(false);
		eierCB.setContentAreaFilled(false);
		schwarmzellenCB.setContentAreaFilled(false);
		nachschaffungszellenCB.setContentAreaFilled(false);
		koenigigJaRad.setContentAreaFilled(false);
		koeniginNeinRad.setContentAreaFilled(false);
		koeniginVielleichtRad.setContentAreaFilled(false);
		leereWabenCB.setContentAreaFilled(false);

		datumNameText.setOpaque(false);
		koeniginJahrText.setOpaque(false);
		raehmchenZahlText.setOpaque(false);
		raehmchenErweitertText.setOpaque(false);
		sonstigeWabenText.setOpaque(false);

		ruhigCB.setFont(new Font(ruhigCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		geschaeftigCB.setFont(new Font(geschaeftigCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		gereiztCB.setFont(new Font(gereiztCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		aggressivCB.setFont(new Font(aggressivCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		honigOffenCB.setFont(new Font(honigOffenCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		honigVerdeckeltCB.setFont(new Font(honigVerdeckeltCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		pollenImStockCB.setFont(new Font(pollenImStockCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		pollenEintragenCB.setFont(new Font(pollenEintragenCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		verkrueppelteBienenCB.setFont(new Font(verkrueppelteBienenCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		drohnenCB.setFont(new Font(drohnenCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		brutMaennlichCB.setFont(new Font(brutMaennlichCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		brutWeiblichCB.setFont(new Font(brutWeiblichCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		eierCB.setFont(new Font(eierCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		schwarmzellenCB.setFont(new Font(schwarmzellenCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		nachschaffungszellenCB.setFont(new Font(nachschaffungszellenCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		koenigigJaRad.setFont(new Font(koenigigJaRad.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		koeniginNeinRad.setFont(new Font(koeniginNeinRad.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		koeniginVielleichtRad.setFont(new Font(koeniginVielleichtRad.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		leereWabenCB.setFont(new Font(leereWabenCB.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		datumNameText.setFont(new Font(datumNameText.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		koeniginJahrText.setFont(new Font(koeniginJahrText.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		raehmchenZahlText.setFont(new Font(raehmchenZahlText.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		raehmchenErweitertText.setFont(new Font(raehmchenErweitertText.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
		sonstigeWabenText.setFont(new Font(sonstigeWabenText.getFont().getFamily(), Font.PLAIN, FONT_SIZE));
	}
}
