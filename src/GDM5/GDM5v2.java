package GDM5;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;

/**
 * Opens an image window and adds a panel below the image
 */
public class GDM5v2 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = { "Original", "Lowpass", "Highpass", "Sharpener"};

	public static void main(String args[]) {

		//IJ.open("C:\\Users\\loxer\\Dropbox\\Eigene Dateien\\Documents\\Studium\\Grundlagen digitaler Medien\\Übung\\5\\sail.jpg");
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

		/**
		 * Change Pixel due to chosed event
		 * @param ip
		 */
		private void changePixelValues(ImageProcessor ip) {

			// 1:1 duplicate Kernel 3x3
			int[] pixels = (int[]) ip.getPixels();

			if (method.equals("Original")) {

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;

						pixels[pos] = origPixels[pos];
					}
				}
			}

			// #Lowpass-Kernel filtering 3x3 (1/9)
			if (method.equals("Lowpass")) {
				int[] soft = soft(pixels);
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						pixels[pos] = soft[pos];
					}
				}
			}

			// #Higpass-Kernel filtering 3x3
			if (method.equals("Highpass")) {
				int[] soft = soft(pixels);
				highpass(soft, pixels);
			}

			// Sharpener-Kernel filter 3x3
			if (method.equals("Sharpener")) {

				int[] soft = soft(pixels);
				int[] high = highpass(soft, pixels);

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int pos = y * width + x;
						int argb = origPixels[pos];
						int highCol = high[pos];

						int r = (argb >> 16) & 0xff;
						int g = (argb >> 8) & 0xff;
						int b = argb & 0xff;

						int rSoft = (highCol >> 16) & 0xff;
						int gSoft = (highCol >> 8) & 0xff;
						int bSoft = highCol & 0xff;

						// Formular: pxl + pxlSoft - 128
						int rn = checkBoundaries(r + rSoft - 128);
						int gn = checkBoundaries(g + gSoft - 128);
						int bn = checkBoundaries(b + bSoft - 128);

						pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
					}
				}
			}			
		}
		private int checkBoundaries(int value) {
			if (value > 255) {
				return 255;
			}
			if (value < 0) {
				return 0;
			}
			return value;
		}

		/**
		 * Lowpass Filtering
		 * 1. Initialize new pixels and set Up your Kernel (3x3)
		 * 2. Write new pixels and divide them by KernelLenght^3 (3x3x§ = 9)
		 */
		private int[] soft(int[] pixels) {
			int[] soft = pixels;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x; // pixel-count by rows

					int rn = 0;
					int gn = 0;
					int bn = 0;

					//3 pixel Kernel - the bigger the Kernel the softer the image
					for (int xN = -1; xN <= 1; xN++) { // 3 rows  (-1 to 1)
						for (int yN = -1; yN <= 1; yN++) { // 3 colums  (-1 to 1)
							int kernelPos = pos + yN + xN;

							// Jumps: last pxl (current row) > first pxl (next row)
							if (kernelPos < 0) {
								kernelPos = pixels.length + kernelPos;
							} else if (kernelPos >= pixels.length) {
								kernelPos = kernelPos - pixels.length;
							}

							int color = origPixels[kernelPos];
							rn += (color >> 16) & 0xff;
							gn += (color >> 8) & 0xff;
							bn += color & 0xff;
						}
					}

					rn = checkBoundaries(rn / 9);
					gn = checkBoundaries(gn / 9);
					bn = checkBoundaries(bn / 9);

					soft[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
				}
			}
			return soft;
		}

		// Returns highpass array with new pixel values
		private int[] highpass(int[] soft, int[] pixels) {

			int[] high = pixels;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pos = y * width + x;
					int argb = origPixels[pos];
					int softPxl = soft[pos];

					int r = (argb >> 16) & 0xff;
					int g = (argb >> 8) & 0xff;
					int b = argb & 0xff;


					int rS = (softPxl >> 16) & 0xff;
					int gS = (softPxl >> 8) & 0xff;
					int bS = softPxl & 0xff;

					// Formular: pxl - pxlSoft +128
					int rn = checkBoundaries(r - rS + 128);
					int gn = checkBoundaries(g - gS + 128);
					int bn = checkBoundaries(b - bS + 128);

					high[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
				}
			}
			return high;
		}
	} // CustomWindow inner class
}