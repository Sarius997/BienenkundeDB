package de.markus.updates;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.commons.io.FileUtils;

import de.markus.BienenkundeDB;
import de.markus.gui.startWindow;

public class Updater {

	// 0 means no connection yet, -1 means connection failure.
	private int latestBdbVersion = 0;
	private int latestLauncherVersion = 0;

	private boolean BdbUpdateAvailable;
	private boolean LauncherUpdateAvailable;

	private String updateUrlBDB;
	private String updateUrlLauncher;

	private String bdbSha512Hash;
	private String launcherSha512Hash;

	private int currentLauncherVersion;

	private static final String pathToFiles = System.getProperty("user.home") + "\\AppData\\Roaming\\BienenkundeDB";
	private static final String installPathJar = pathToFiles + "\\BienenkundeDB.jar";
	private static final String installPathExe = pathToFiles + "\\BienenkundeDB Launcher.exe";

	private boolean isExeInstalled = false;
	private boolean isJarInstalled = false;

	public Updater() {

	}

	public void updateNotification(boolean calledByUser) {
		boolean bothOutdated = isBdbUpdateAvailable() && isLauncherUpdateAvailable();
		final JFrame updateDlg = new JFrame();
		updateDlg.setResizable(false);

		updateDlg.setIconImage(new ImageIcon(startWindow.propHandler.getIcon()).getImage());
		
		JPanel buttons = new JPanel();
		JLabel message = new JLabel("Wollen Sie " + (bothOutdated ? "die Updates" : "das Update")
				+ " herunterladen? Alte Versionen werden gelöscht!");
		JButton bdbUpdate = new JButton("BienenkundeDB");
		bdbUpdate.setToolTipText("BienenkundeDB Update herunterladen");
		JButton launcherUpdate = new JButton("Launcher");
		launcherUpdate.setToolTipText("Launcher Update herunterladen");
		JButton bothUpdate = new JButton("Beides");
		bothUpdate.setToolTipText("BienenkundeDB und Launcher Update herunterladen");
		JButton cancelUpdate = new JButton("Abbrechen");
		cancelUpdate.setToolTipText((bothOutdated ? "Die Updates können" : "Das Update kann")
				+ " zu einem späteren Zeitpunkt heruntergeladen werden.");
		JButton btnOk = new JButton("OK");

		Dimension prefSize = new Dimension(bdbUpdate.getPreferredSize().width, bdbUpdate.getPreferredSize().height);
		Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();
		if (isBdbUpdateAvailable()) {
			if (isLauncherUpdateAvailable()) {
				updateDlg.setTitle("Es ist ein BienenkundeDB und Launcher Update verfügbar");
				updateDlg.setLayout(new BorderLayout());
				buttons.setLayout(new GridLayout(0, 4));
				updateDlg.add(message, BorderLayout.NORTH);
				updateDlg.add(new JSeparator(), BorderLayout.CENTER);
				buttons.add(bothUpdate);
				buttons.add(bdbUpdate);
				buttons.add(launcherUpdate);
				buttons.add(cancelUpdate);

				updateDlg.add(buttons, BorderLayout.SOUTH);
				updateDlg.setSize(prefSize.width * 4 + 30, prefSize.height * 3);
				updateDlg.setLocation((displaySize.width / 2 - updateDlg.getWidth() / 2),
						(displaySize.height / 2 - updateDlg.getHeight() / 2));
			} else {
				updateDlg.setTitle("Es ist ein BienenkundeDB Update verfügbar");
				updateDlg.setLayout(new BorderLayout());
				buttons.setLayout(new GridLayout(0, 2));
				updateDlg.add(message, BorderLayout.NORTH);
				updateDlg.add(new JSeparator(), BorderLayout.CENTER);
				buttons.add(bdbUpdate);
				buttons.add(cancelUpdate);

				updateDlg.add(buttons, BorderLayout.SOUTH);
				updateDlg.setSize(message.getPreferredSize().width + 30, prefSize.height * 3);
				updateDlg.setLocation((displaySize.width / 2 - updateDlg.getWidth() / 2),
						(displaySize.height / 2 - updateDlg.getHeight() / 2));
			}
		} else {
			if (isLauncherUpdateAvailable()) {
				updateDlg.setTitle("Es ist ein Launcher Update verfügbar");
				updateDlg.setLayout(new BorderLayout());
				buttons.setLayout(new GridLayout(0, 2));
				updateDlg.add(message, BorderLayout.NORTH);
				updateDlg.add(new JSeparator(), BorderLayout.CENTER);
				buttons.add(launcherUpdate);
				buttons.add(cancelUpdate);

				updateDlg.add(buttons, BorderLayout.SOUTH);
				updateDlg.setSize(message.getPreferredSize().width + 30, prefSize.height * 3);
				updateDlg.setLocation((displaySize.width / 2 - updateDlg.getWidth() / 2),
						(displaySize.height / 2 - updateDlg.getHeight() / 2));
			} else {
				if (calledByUser) {
					// updateDlg.setTitle("Es ist kein Update verfügbar");
					updateDlg.setLayout(new GridLayout(0, 1));
					message.setText("Ihre Version ist bereits aktuell");
					// JLabel lab = new JLabel("gerade nicht auf das Netzwerk
					// zugegriffen werden.");

					updateDlg.add(message);
					// updateDlg.add(lab);
					updateDlg.add(btnOk);
					updateDlg.setSize(message.getPreferredSize().width + 20, btnOk.getPreferredSize().height * 4);
					updateDlg.setLocation((displaySize.width / 2 - updateDlg.getWidth() / 2),
							(displaySize.height / 2 - updateDlg.getHeight() / 2));
				}
			}
		}

		// final JFrame download = new JFrame("Datei" + (bothOutdated ? "en
		// werden " : " wird ") + "heruntergeladen");
		// download.setResizable(false);
		// download.setSize(300, 150);
		// download.setLocation((displaySize.height / 2 - download.getHeight() /
		// 2),
		// (displaySize.width / 2 - download.getWidth() / 2));
		// final JProgressBar downBar = new JProgressBar();
		// downBar.setSize(250, 50);
		// downBar.setMinimum(0);
		// downBar.setMaximum(255);
		// downBar.setStringPainted(true);
		//
		// download.add(downBar);

		bothUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDlg.setVisible(false);
				// int maxBdb = 0;
				// int maxLauncher = 0;
				// try {
				// maxBdb = new
				// URL(updateUrlBDB).openConnection().getContentLength() / 1024;
				// maxLauncher = new
				// URL(updateUrlLauncher).openConnection().getContentLength() /
				// 1024;
				// } catch (IOException e1) {
				// e1.printStackTrace();
				// }
				// downBar.setMaximum(maxBdb + maxLauncher);
				// downBar.setString("BienenkundeDB.jar");
				// download.add(downBar);
				// download.setVisible(true);
				UpdateCodes bdbUpdate = downloadBdbUpdate();
				// downBar.setValue(maxBdb);
				// downBar.setString("BienenkundeDB Launcher.exe");
				UpdateCodes launcherUpdate = downloadLauncherUpdate();
				// downBar.setValue(maxBdb + maxLauncher);
				// downBar.setString("fertig");

				// download.setVisible(false);

				handleUpdateCodes(bdbUpdate, true, true);
				handleUpdateCodes(launcherUpdate, false, false);
			}
		});
		bdbUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDlg.setVisible(false);
				// int maxBdb = 0;
				// try {
				// maxBdb = new
				// URL(updateUrlBDB).openConnection().getContentLength() / 1024;
				// System.out.println(maxBdb);
				// } catch (IOException e1) {
				// e1.printStackTrace();
				// }
				// downBar.setMaximum(maxBdb);
				// downBar.setString("BienenkundeDB.jar");
				// download.add(downBar);
				// downBar.setVisible(true);
				// download.setVisible(true);
				UpdateCodes bdbUpdate = downloadBdbUpdate();
				// downBar.setValue(maxBdb);
				// downBar.setString("fertig");

				// download.setVisible(false);

				handleUpdateCodes(bdbUpdate, true, false);
			}
		});
		launcherUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDlg.setVisible(false);
				// int maxLauncher = 0;
				// try {
				// maxLauncher = new
				// URL(updateUrlLauncher).openConnection().getContentLength() /
				// 1024;
				// } catch (IOException e1) {
				// e1.printStackTrace();
				// }
				// downBar.setMaximum(maxLauncher);
				// downBar.setString("BienenkundeDB Launcher.exe");
				// download.add(downBar);
				// download.setVisible(true);
				UpdateCodes launcherUpdateCodes = downloadLauncherUpdate();
				// downBar.setValue(maxLauncher);
				// downBar.setString("fertig");

				// download.setVisible(false);

				handleUpdateCodes(launcherUpdateCodes, false, false);
			}
		});
		cancelUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDlg.setVisible(false);
			}
		});
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDlg.setVisible(false);
			}
		});

		if (isBdbUpdateAvailable() || isLauncherUpdateAvailable() || calledByUser)
			updateDlg.setVisible(true);
	}

	private void handleUpdateCodes(UpdateCodes code, boolean isJar, boolean bothUpdated) {
		if (isJar) {
			switch (code) {
			case malformedLink:
			case downloadFailed:
			case corruptedDownload:
				updateFailed(isJar);
				break;
			case updateOk:
				if (!bothUpdated)
					restart(isJar);
				break;
			case fuckingHackerLeaveMyPrivateMethodsAlone:
				System.exit(404);
			default:
				break;
			}
		} else {
			switch (code) {
			case malformedLink:
			case downloadFailed:
			case corruptedDownload:
				updateFailed(isJar);
				break;
			case updateOk:
				if (!bothUpdated)
					restart(isJar);
				break;
			case fuckingHackerLeaveMyPrivateMethodsAlone:
				System.exit(404);
			default:
				break;
			}
		}
	}

	private void updateFailed(final boolean isJar) {
		try {
			// String filePath = "";
			// if (isJar) {
			// if (isJarInstalled) {
			// filePath = installPathJar;
			// } else {
			// String pathHelper =
			// BienenkundeDB.class.getProtectionDomain().getCodeSource().getLocation().toURI()
			// .getPath();
			// pathHelper = pathHelper.substring(1);
			// pathHelper = pathHelper.replace("/", "\\");
			// filePath = pathHelper;
			// }
			// } else {
			// filePath = installPathExe;
			// }
			final JFrame frame = new JFrame("Das Update ist Fehlgeschlagen");
			frame.setResizable(false);

			frame.setIconImage(new ImageIcon(startWindow.propHandler.getIcon()).getImage());
			
			JLabel msg1 = new JLabel("Das Update wurde rückgängig gemacht.");
			JLabel msg2 = new JLabel("Bitte versuchen Sie es später erneut oder");
			JLabel msg3 = new JLabel("laden sie sich die Datei hier herunter:");
			JButton lnk = new JButton("Download");
			JLabel msg4 = new JLabel("Und speichern Sie die Datei hier ab:");
			JButton msg5 = new JButton("Speicherordner");
			JButton ok = new JButton("OK");

			if (isJar) {
				// This is not supported by JVM -> cannot rename jar file while
				// it is running
				// if (isJarInstalled) {
				// File oldVer = new File(installPathJar + ".old");
				// File destFile = new File(installPathJar);
				// FileUtils.moveFile(oldVer, destFile);
				// } else {
				// File oldVer = new File(filePath + ".old");
				// File destFile = new File(filePath);
				// FileUtils.moveFile(oldVer, destFile);
				// }
			} else {
				if (isExeInstalled) {
					File oldVer = new File(installPathExe + ".old");
					File destFile = new File(installPathExe);
					FileUtils.moveFile(oldVer, destFile);
				}
			}
			frame.setLayout(new GridLayout(0, 1));
			frame.add(msg1);
			frame.add(msg2);
			frame.add(msg3);
			frame.add(lnk);
			frame.add(msg4);
			frame.add(msg5);
			frame.add(ok);

			Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();

			frame.setSize(msg2.getPreferredSize().width + 80, ok.getPreferredSize().height * 8);
			frame.setLocation((displaySize.width / 2 - frame.getWidth() / 2),
					(displaySize.height / 2 - frame.getHeight() / 2));

			lnk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
						try {
							desktop.browse(new URI(isJar ? updateUrlBDB : updateUrlLauncher));
						} catch (Exception g) {
							g.printStackTrace();
						}
					}
				}
			});
			msg5.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						System.out.println(BienenkundeDB.dateTime() + "Trying to open the save folder");
						Desktop.getDesktop().open(new File(pathToFiles));
					} catch (IOException e1) {
						System.out.println(BienenkundeDB.dateTime());
						e1.printStackTrace();
					}
				}
			});
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(false);
				}
			});

			frame.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void restart(final boolean onlyJar) {
		final Dimension displaySize = Toolkit.getDefaultToolkit().getScreenSize();
		final JFrame frame = new JFrame("Neu Starten");

		frame.setResizable(false);
		
		frame.setIconImage(new ImageIcon(startWindow.propHandler.getIcon()).getImage());
		
		if (isExeInstalled) {
			frame.setLayout(new GridLayout(0, 2));
			JButton btnNow = new JButton("Jetzt");
			btnNow.setToolTipText("Startet das Programm jetzt neu");
			JButton btnLater = new JButton("Später");
			btnLater.setToolTipText("Beim nächsten Programmstart wird die neue Version verwendet");

			frame.add(btnNow);
			frame.add(btnLater);

			frame.setSize(btnLater.getPreferredSize().width * 2 + 80, btnLater.getPreferredSize().height * 2);
			frame.setLocation((displaySize.width / 2 - frame.getWidth() / 2),
					(displaySize.height / 2 - frame.getHeight() / 2));

			btnNow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog dlg = new JDialog(frame, "Achtung");
					
					
					dlg.setLayout(new GridLayout(0, 1));
					dlg.setResizable(false);
					JLabel warn1 = new JLabel("Speichern Sie ihre Änderungen vor dem Neustart,");
					JLabel warn2 = new JLabel("da diese sonst verloren gehen!!!");
					JLabel warn3 = new JLabel("Sie können dazu einfach den aktuellen Eintrag");
					JLabel warn4 = new JLabel("fertig stellen, speichern und dann das Programm");
					JLabel warn5 = new JLabel("über diesen Dialog neu starten.");
					JButton btnRestart = new JButton("Jetzt Neustarten");

					dlg.add(warn1);
					dlg.add(warn2);
					dlg.add(warn3);
					dlg.add(warn4);
					dlg.add(warn5);
					dlg.add(btnRestart);

					dlg.setSize(warn1.getPreferredSize().width + 30, btnRestart.getPreferredSize().height * 7);
					dlg.setLocation((displaySize.width / 2 - dlg.getWidth() / 2),
							(displaySize.height / 2 - dlg.getHeight() / 2));

					btnRestart.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (onlyJar) {
								System.exit(5);
							} else {
								System.exit(6);
							}
						}
					});

					dlg.setVisible(true);
				}
			});
			btnLater.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(false);
				}
			});
		} else {
			frame.setLayout(new GridLayout(0, 1));
			String pathHelper = "";
			try {
				pathHelper = BienenkundeDB.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			pathHelper = pathHelper.substring(1);
			pathHelper = pathHelper.replace("/", "\\");
			pathHelper = pathHelper.replace(".jar", "_new.jar");
			String[] path = pathHelper.split("\\\\");
			String jarName = path[path.length - 1];

			JLabel msg1 = new JLabel("Sie verwenden die Version ohne Launcher.");
			JLabel msg2 = new JLabel("Starten Sie nun " + jarName);
			JLabel msg3 = new JLabel("um die neue Version zu verwenden.");
			JLabel msg4 = new JLabel("Die alte Version kann nun gelöscht und");
			JLabel msg5 = new JLabel("die neue Datei umbenannt werden.");
			JLabel msg6 = new JLabel("Wenn die neue Datei nicht umbenannt wird, können");
			JLabel msg7 = new JLabel("Updates nicht mehr automatisch heruntergeladen werden.");
			JButton btnOk = new JButton("OK");

			frame.add(msg1);
			frame.add(msg2);
			frame.add(msg3);
			frame.add(msg4);
			frame.add(msg5);
			frame.add(msg6);
			frame.add(msg7);
			frame.add(btnOk);

			frame.setSize(msg7.getPreferredSize().width + 30, btnOk.getPreferredSize().height * 9);
			frame.setLocation((displaySize.width / 2 - frame.getWidth() / 2),
					(displaySize.height / 2 - frame.getHeight() / 2));

			btnOk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(false);
				}
			});
		}
		frame.setVisible(true);
	}

	public void checkForUpdates(final boolean calledByUser) {
		if (latestBdbVersion <= 1 || latestLauncherVersion <= 1) {
			Thread thread = new Thread() {
				public void run() {
					try {
						int currentBDBVersion = Integer.parseInt(BienenkundeDB.VERSION_NUMBER);
						currentLauncherVersion = 0;

						File bdbFile = new File(installPathJar);
						isJarInstalled = bdbFile.exists();

						File launcherFile = new File(installPathExe);
						isExeInstalled = launcherFile.exists();

						if (BienenkundeDB.LAUNCHER_VERSION_NUMBER != null) {
							try {
								currentLauncherVersion = Integer.parseInt(BienenkundeDB.LAUNCHER_VERSION_NUMBER);
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}

						if (isExeInstalled && currentLauncherVersion == 0) {
							@SuppressWarnings("unused")
							Process p = Runtime.getRuntime().exec(installPathExe + " getVersionInfo");
							Thread.sleep(4000);
							File infoFile = new File(pathToFiles + "\\launcherVerInfo.txt");
							if (infoFile.exists()) {
								FileInputStream fis = new FileInputStream(infoFile);
								byte[] data = new byte[(int) infoFile.length()];
								fis.read(data);
								fis.close();
								try {
									currentLauncherVersion = Integer.parseInt(new String(data, "UTF-8"));
								} catch (NumberFormatException e) {
									e.printStackTrace();
								}
								infoFile.delete();
							}
						}

						URL versionInfo = new URL(
								"https://raw.githubusercontent.com/Sarius997/BienenkundeDB/master/BienenkundeDB-java/BienenkundeDB%20Desktop%20Version/src/images/VersionInfo.txt");
						BufferedReader inforeader = new BufferedReader(new InputStreamReader(versionInfo.openStream()));

						latestBdbVersion = Integer.parseInt(inforeader.readLine());

						if (latestBdbVersion > currentBDBVersion) {
							setBdbUpdateAvailable(true);
						} else {
							setBdbUpdateAvailable(false);
						}

						if (isExeInstalled) {
							latestLauncherVersion = Integer.parseInt(inforeader.readLine());

							if (latestLauncherVersion > currentLauncherVersion) {
								setLauncherUpdateAvailable(true);
							} else {
								setLauncherUpdateAvailable(false);
							}
						} else {
							inforeader.readLine();
							setLauncherUpdateAvailable(false);
						}

						setUpdateUrlBDB(inforeader.readLine());
						setUpdateUrlLauncher(inforeader.readLine());

						setBdbSha512Hash(inforeader.readLine());
						setLauncherSha512Hash(inforeader.readLine());

						System.out.println(BienenkundeDB.dateTime() + "Checking for update");
						System.out.println("\tcurrent BDB Version: \t\t" + currentBDBVersion);
						System.out.println("\tlatest BDB Version: \t\t" + latestBdbVersion);
						System.out.println("\tcurrentLauncherVersion: \t" + currentLauncherVersion);
						System.out.println("\tlatest Launcher Version: \t" + latestLauncherVersion);
						System.out.println("\tBDB update available: \t\t" + BdbUpdateAvailable);
						System.out.println("\tLauncher update available: \t" + LauncherUpdateAvailable);
						System.out.println("\tBDB update URL: \t\t\t" + updateUrlBDB);
						System.out.println("\tLauncher update URL: \t\t" + updateUrlLauncher);
						System.out.println("\tBDB SHA-512 CheckSum: \t\t" + bdbSha512Hash);
						System.out.println("\tLauncher SHA-512 CheckSum: \t" + launcherSha512Hash);

						inforeader.close();

						updateNotification(calledByUser);
					} catch (Exception e) {
						latestBdbVersion = -1;
						latestLauncherVersion = -1;
						e.printStackTrace();
					}
				}
			};
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private UpdateCodes downloadBdbUpdate() {
		try {
			if (isBdbUpdateAvailable()) {
				if (isJarInstalled) {
					// This is not supported by JVM -> cannot rename jar file
					// while it is running
					// File oldBdbVer = new File(installPathJar);
					// File renamed = new File(installPathJar + ".old");
					// FileUtils.moveFile(oldBdbVer, renamed);
					File newBdbVer = new File(pathToFiles + "\\BienenkundeDB_neu.jar");
					FileUtils.copyURLToFile(new URL(updateUrlBDB), newBdbVer);

					if (!newBdbVer.exists())
						return UpdateCodes.downloadFailed;
					String actualSha512Hash = getSha512CheckSum(pathToFiles + "\\BienenkundeDB_neu.jar");
					if (actualSha512Hash.compareTo(getBdbSha512Hash()) == 0) {
						// File del = new File(installPathJar + ".old");
						// del.delete();
						return UpdateCodes.updateOk;
					} else {
						newBdbVer.delete();
						return UpdateCodes.corruptedDownload;
					}
				} else {
					String pathHelper = BienenkundeDB.class.getProtectionDomain().getCodeSource().getLocation().toURI()
							.getPath();
					pathHelper = pathHelper.substring(1);
					pathHelper = pathHelper.replace("/", "\\");
					pathHelper = pathHelper.replace(".jar", "_new.jar");
					String jarPath = pathHelper;

					// This is not supported by JVM -> cannot rename jar file
					// while it is running
					// File oldBdbVer = new File(jarPath);
					// File renamed = new File(jarPath + ".old");
					// FileUtils.moveFile(oldBdbVer, renamed);

					File newBdbVer = new File(jarPath);
					FileUtils.copyURLToFile(new URL(updateUrlBDB), newBdbVer);

					if (!newBdbVer.exists())
						return UpdateCodes.downloadFailed;
					String actualSha512Hash = getSha512CheckSum(jarPath);
					if (actualSha512Hash.compareTo(getBdbSha512Hash()) == 0) {
						// File del = new File(jarPath + ".old");
						// del.delete();
						return UpdateCodes.updateOk;
					} else {
						newBdbVer.delete();
						return UpdateCodes.corruptedDownload;
					}
				}
			} else {
				return UpdateCodes.fuckingHackerLeaveMyPrivateMethodsAlone;
			}
		} catch (MalformedURLException | URISyntaxException e) {
			e.printStackTrace();
			return UpdateCodes.malformedLink;
		} catch (IOException e) {
			e.printStackTrace();
			return UpdateCodes.downloadFailed;
		}
	}

	private UpdateCodes downloadLauncherUpdate() {
		try {
			if (isLauncherUpdateAvailable()) {
				// This works as the .exe can be renamed while it is executed
				File oldLauncherVer = new File(installPathExe);
				File renamed = new File(installPathExe + ".old");
				FileUtils.moveFile(oldLauncherVer, renamed);
				File newLauncherVer = new File(installPathExe);
				FileUtils.copyURLToFile(new URL(updateUrlLauncher), newLauncherVer);

				if (!newLauncherVer.exists())
					return UpdateCodes.downloadFailed;
				String actualSha512Hash = getSha512CheckSum(installPathExe);
				if (actualSha512Hash.compareTo(getLauncherSha512Hash()) == 0) {
					File del = new File(installPathExe + ".old");
					del.delete();
					return UpdateCodes.updateOk;
				} else {
					return UpdateCodes.corruptedDownload;
				}
			} else {
				return UpdateCodes.fuckingHackerLeaveMyPrivateMethodsAlone;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return UpdateCodes.malformedLink;
		} catch (IOException e) {
			e.printStackTrace();
			return UpdateCodes.downloadFailed;
		}
	}

	public int getLatestBdbVersion() {
		return latestBdbVersion;
	}

	public void resetLatestBdbVersion() {
		latestBdbVersion = 0;
	}

	public int getLatestLauncherVersion() {
		return latestLauncherVersion;
	}

	public void resetLatestLauncherVersion() {
		latestLauncherVersion = 0;
	}

	public boolean isBdbUpdateAvailable() {
		return BdbUpdateAvailable;
	}

	private void setBdbUpdateAvailable(boolean bdbUpdateAvailable) {
		BdbUpdateAvailable = bdbUpdateAvailable;
	}

	public boolean isLauncherUpdateAvailable() {
		return LauncherUpdateAvailable;
	}

	private void setLauncherUpdateAvailable(boolean launcherUpdateAvailable) {
		LauncherUpdateAvailable = launcherUpdateAvailable;
	}

	public String getUpdateUrlBDB() {
		return updateUrlBDB;
	}

	private void setUpdateUrlBDB(String updateUrlBDB) {
		this.updateUrlBDB = updateUrlBDB;
	}

	public String getUpdateUrlLauncher() {
		return updateUrlLauncher;
	}

	private void setUpdateUrlLauncher(String updateUrlLauncher) {
		this.updateUrlLauncher = updateUrlLauncher;
	}

	public String getBdbSha512Hash() {
		return bdbSha512Hash;
	}

	private void setBdbSha512Hash(String bdbSha512Hash) {
		this.bdbSha512Hash = bdbSha512Hash;
	}

	public String getLauncherSha512Hash() {
		return launcherSha512Hash;
	}

	private void setLauncherSha512Hash(String launcherSha512Hash) {
		this.launcherSha512Hash = launcherSha512Hash;
	}

	private String getSha512CheckSum(String fileName) {
		try {
			MessageDigest dig = MessageDigest.getInstance("SHA-512");
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(fileName);
			byte[] dataBytes = new byte[1024];

			int nread = 0;

			while ((nread = fis.read(dataBytes)) != -1) {
				dig.update(dataBytes, 0, nread);
			}

			byte[] digBytes = dig.digest();

			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < digBytes.length; i++) {
				sb.append(Integer.toString((digBytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			System.out.println(dig.getAlgorithm() + " hash for \"" + fileName + "\" is: " + sb.toString());
			return sb.toString();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
