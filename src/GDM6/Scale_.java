package GDM6;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Scale_ implements PlugInFilter {

	ImagePlus imp; // ImagePlus object
	ImagePlus neu; // new Picture

	public static void main(String args[]) {

		IJ.open("C:\\Users\\loxer\\Dropbox\\Eigene Dateien\\Documents\\Studium\\Grundlagen digitaler Medien\\Übung\\6\\component.jpg");
		// IJ.open("Z:/Pictures/Beispielbilder/component.jpg");

		Scale_ pw = new Scale_();
		pw.imp = IJ.getImage();
		pw.run(null);
	}

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_RGB + NO_CHANGES;
		// kann RGB-Bilder und veraendert das Original nicht
	}

	public void run(ImageProcessor ip) {

		ip = imp.getProcessor();

		String[] dropdownmenue = { "Kopie", "Pixelwiederholung", "Bilinear" };

		GenericDialog gd = new GenericDialog("scale");
		gd.addChoice("Methode", dropdownmenue, dropdownmenue[2]);
		gd.addNumericField("Hoehe:", 125, 0);
		gd.addNumericField("Breite:", 115, 0);

		gd.showDialog();

		int width_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
		int height_n = (int) gd.getNextNumber();

		int width = ip.getWidth(); // Breite bestimmen
		int height = ip.getHeight(); // Hoehe bestimmen

		double factorScaleWidth = (double) width / width_n;
		double factorScaleHeight = (double) height / height_n;

		// height_n = height;
		// width_n = width;

		neu = NewImage.createRGBImage("Skaliertes Bild", width_n, height_n, 1, NewImage.FILL_BLACK);

		ImageProcessor ip_n = neu.getProcessor();

		int[] pix = (int[]) ip.getPixels();
		int[] pix_n = (int[]) ip_n.getPixels();
		String chosenCheckbox = gd.getChoices().toString();

		// System.out.println("Methode: " + gd.getChoices());

		// Schleife ueber das neue Bild

		if (chosenCheckbox.contains("current=Kopie")) {
			for (int y_n = 0; y_n < height_n; y_n++) {
				for (int x_n = 0; x_n < width_n; x_n++) {

					int y = y_n;
					int x = x_n;

					if (y < height && x < width) {
						int pos_n = y_n * width_n + x_n;
						int pos = y * width + x;

						pix_n[pos_n] = pix[pos];
					}
				}
			}
		}

		if (chosenCheckbox.contains("current=Pixelwiederholung")) {
			for (int y_n = 0; y_n < height_n; y_n++) {
				for (int x_n = 0; x_n < width_n; x_n++) {

					double y = Math.round(y_n * factorScaleHeight);
					double x = Math.round(x_n * factorScaleWidth);

					if (y >= height) {
						y = height - 1;
					}

					if (x >= width) {
						x = width - 1;
					}

					int pos_n = y_n * width_n + x_n;
					int pos = (int) (y * width + x);

					pix_n[pos_n] = pix[pos];
				}
			}
		}

		if (chosenCheckbox.contains("current=Bilinear")) {

			System.out.println("FactorScaleWidth: 	" + factorScaleWidth);
			System.out.println("FactorScaleHeight: 	" + factorScaleHeight);
			System.out.println("pix: 			" + pix.length);
			System.out.println("pix_n:  		" + pix_n.length);

			for (int y_n = 0; y_n < height_n; y_n++) {
				for (int x_n = 0; x_n < width_n; x_n++) {

					double y = Math.round(y_n * factorScaleHeight);
					double x = Math.round(x_n * factorScaleWidth);

					if (y >= height) {
						y = height - 1;
					}

					if (x >= width) {
						x = width - 1;
					}

					if (x_n == 100 && y_n == 100) {
						System.out.println("Show yourself!");
					}
					
					double r_n = 0;
					double g_n = 0;
					double b_n = 0;

					for (int y_bilinear = (int) y; y_bilinear <= y_bilinear + 1; y++) {
						for (int x_bilinear = (int) x; x_bilinear <= x_bilinear + 1; x++) {

							// calculating factor of bilinear interpolation
							// by considering the current position in the loop
							double xCheck = 1;
							double yCheck = 1;							

							if (x_bilinear != (int) x) {
								xCheck = 0;
								if (x_n == 100 && y_n == 100) System.out.println("Show yourself!");
							}

							if (y_bilinear != (int) y) {
								yCheck = 0;
								if (x_n == 100 && y_n == 100) System.out.println("Show yourself!");
							}

							double factor = xCheck - (x - x_bilinear) + yCheck - (y - y_bilinear);

							// calculating position by considering the borders
							int pos = 0;
							if (y_bilinear >= height && x_bilinear >= width) { // check bottom right corner
								pos = (y_bilinear - 2) * width + x_bilinear - 2;
								System.out.println("Show yourself!");
							} else if (y_bilinear >= height && x_bilinear < width) { // check bottom line
								pos = (y_bilinear - 2) * width + x_bilinear;
							} else if (y_bilinear < height && x_bilinear >= width) { // check right-hand side
								pos = y_bilinear * width + x_bilinear - 2;
							} else {
								pos = y_bilinear * width + x_bilinear;
							}

							int argb = pix[pos];

							int r = (argb >> 16) & 0xff;
							int g = (argb >> 8) & 0xff;
							int b = argb & 0xff;

							r_n += r * factor;
							g_n += g * factor;
							b_n += b * factor;
						}
					}

					int pos_n = y_n * width_n + x_n;
					int r = normalize(r_n);
					int g = normalize(g_n);
					int b = normalize(b_n);

					pix_n[pos_n] = (0xFF << 24) | (r << 16) | (g << 8) | (int) b;
				}
			}
		}
		displayNewPicture();
	}

	private int normalize(double value) {
		if (value > 255) {
			return 255;
		}
		return (int) value;
	}

	void displayNewPicture() {
		neu.show();
		neu.updateAndDraw();
	}

	void showAbout() {
		IJ.showMessage("");
	}
}
