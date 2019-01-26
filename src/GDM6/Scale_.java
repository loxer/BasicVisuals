package GDM6;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.lang.Math.*;

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
		gd.addChoice("Methode", dropdownmenue, dropdownmenue[1]);
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

			System.out.println("FactorScaleWidth: 	" + factorScaleWidth);
			System.out.println("FactorScaleHeight: 	" + factorScaleHeight);
			System.out.println("pix: 			" + pix.length);
			System.out.println("pix_n:  		" + pix_n.length);

			for (int y_n = 0; y_n < height_n; y_n++) {
				for (int x_n = 0; x_n < width_n; x_n++) {

					int y = (int) (y_n * factorScaleHeight);
					int x = (int) (x_n * factorScaleWidth);

					int pos_n = y_n * width_n + x_n;
					int pos = y * width + x;

					pix_n[pos_n] = pix[pos];
				}
			}
		}

		if (chosenCheckbox.contains("current=Bilinear")) {

		}
		displayNewPicture();
	}

	void displayNewPicture() {
		neu.show();
		neu.updateAndDraw();
	}

	void showAbout() {
		IJ.showMessage("");
	}
}
