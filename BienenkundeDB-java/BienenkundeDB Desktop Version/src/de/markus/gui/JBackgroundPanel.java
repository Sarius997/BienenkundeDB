package de.markus.gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.markus.PropertiesHandler;

@SuppressWarnings("serial")
public class JBackgroundPanel extends JPanel {
	   private ImageIcon imageIcon;

	    public JBackgroundPanel() {
	        // load the background image
	    	PropertiesHandler propHandler = new PropertiesHandler();
			imageIcon = new ImageIcon(propHandler.getBackground());
			Image image = imageIcon.getImage();
			Image newImage = image.getScaledInstance(500, 700, Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(newImage);
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        // paint the background image and scale it to fill the entire space
	        g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
	    }
}
