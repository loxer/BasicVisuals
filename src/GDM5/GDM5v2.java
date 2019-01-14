package GDM5;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Opens an image window and adds a panel below the image
 */
public class GDM5v2 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = { "Original", "Weich", "Hochpass", "Starke Kanten"};

	public static void main(String args[]) {

//		IJ.open("C:\\Users\\loxer\\Dropbox\\Eigene Dateien\\Documents\\Studium\\Grundlagen digitaler Medien\\‹bung\\5\\sail.jpg");
		IJ.open("..\\Files\\sail.jpg");
		GDM5v2 pw = new GDM5v2();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp == null)
			imp = WindowManager.getCurrentImage();
		if (imp == null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}

	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int[]) ip.getPixels()).clone();
	}

	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class

	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			// JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			}

		}

		private void changePixelValues(ImageProcessor ip) {

			// Array zum Zur√ºckschreiben der Pixelwerte
			int[] pixels = (int[]) ip.getPixels();

			if (method.equals("Original")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;

						pixels[pos] = origPixels[pos];
					}
				}
			}

			// Weichgezeichnetes Bild 3x3 Mittelwertfilter (1/9)
			if (method.equals("Weich")) {
				int[] weich = soft(pixels);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						pixels[pos] = weich[pos];
					}
				}
			}

			// Hochpassgefiltertes Bild
			if (method.equals("Hochpass")) {
				int[] weich = soft(pixels);
				hochpass(weich, pixels);
			}

			// Bild mit verst‰rkten Kanten oder Hochpass
			if (method.equals("Starke Kanten")) {

				int[] weich = soft(pixels);
				int[] hoch = hochpass(weich, pixels);

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos]; // Lesen der Originalwerte
						int hochCol = hoch[pos];

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						int rW = (hochCol >> 16) & 0xff;
						int gW = (hochCol >> 8) & 0xff;
						int bW = hochCol & 0xff;

						int rn = checkBoundaries(r + rW - 128);
						int gn = checkBoundaries(g + gW - 128);
						int bn = checkBoundaries(b + bW - 128);

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}			
		}

		// um fehler zu vermeiden
		private int checkBoundaries(int value) {
			if (value > 255) {
				return 255;
			}
			if (value < 0) {
				return 0;
			}
			return value;
		}

		// returnt array mit tiefpassgefilterten pixeln
		private int[] soft(int[] pixels) {
			int[] weich = pixels;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;

					int rn = 0;
					int gn = 0;
					int bn = 0;

					for (int xN = -1; xN <= 1; xN++) {
						for (int yN = -1; yN <= 1; yN++) {
							int kernelPos = pos + yN + xN;
							// auﬂerhalb des bildes springt auf andere seite
							if (kernelPos < 0) {
								kernelPos = pixels.length + kernelPos;
							} else if (kernelPos >= pixels.length) {
								kernelPos = kernelPos - pixels.length;
							}

							// 9er kernel speichern
							int color = origPixels[kernelPos];
							rn += (color >> 16) & 0xff;
							gn += (color >> 8) & 0xff;
							bn += color & 0xff;
						}
					}

					// 1/9 da insgesamt 1 rauskommen soll
					rn = checkBoundaries(rn / 9);
					gn = checkBoundaries(gn / 9);
					bn = checkBoundaries(bn / 9);

					weich[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
				}
			}
			return weich;
		}

		// returnt hochpass gefiltertes array
		private int[] hochpass(int[] weich, int[] pixels) {

			int[] hoch = pixels;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;
					int argb = origPixels[pos]; // Lesen der Originalwerte
					// verwendet weichgezeichnetes array
					int weichCol = weich[pos];

					int r = (argb >> 16) & 0xff;
					int g = (argb >> 8) & 0xff;
					int b = argb & 0xff;

					int rW = (weichCol >> 16) & 0xff;
					int gW = (weichCol >> 8) & 0xff;
					int bW = weichCol & 0xff;

					int rn = checkBoundaries(r - rW + 128);
					int gn = checkBoundaries(g - gW + 128);
					int bn = checkBoundaries(b - bW + 128);

					hoch[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
				}
			}
			return hoch;
		}
	} // CustomWindow inner class
}