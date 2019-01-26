package GDM1;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

// erste Uebung (elementare Bilderzeugung) bearbeitet von Sebastian Puetz

public class GDM1 implements PlugIn {

	final static String[] choices = { "Schwarzes Bild", "Gelbes Bild", "Vertikaler Schwarz/Weiss Verlauf",
			"Horrizontaler Schwarz/Weiss Verlauf", "Vertikaler Schwarz/Weiss/Schwarz Verlauf",
			"Horrizontaler Schwarz/Weiss/Schwarz Verlauf", "Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
			"FranzÃ¶sische Flagge", "Bahamische Flagge", "Tschechiche Flagge", "Bangladeschische Flagge",
			"Japanische Flagge", "Japanische Flagge mit weichen Kanten", "Probeklausur Aufgabe 7" };

	private String choice;

	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
		ij.exitWhenQuitting(true);

		GDM1 imageGeneration = new GDM1();
		imageGeneration.run("");
	}

	// testing commits
	public void run(String arg) {
		// System.out.println("test");
		int width = 400; // Breite
		int height = 400; // Hoehe

		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();

		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[]) ip.getPixels();

		dialog();

		////////////////////////////////////////////////////////////////

		if (choice.equals("Schwarzes Bild")) {
			generateBlackImage(width, height, pixels);
		}

		if (choice.equals("Gelbes Bild")) {
			generateYellowImage(width, height, pixels);
		}

		if (choice.equals("FranzÃ¶sische Flagge")) {
			generateFrFlag(width, height, pixels);
		}

		if (choice.equals("Vertikaler Schwarz/Weiss Verlauf")) {
			generateCourseVerticalSW(width, height, pixels);
		}

		if (choice.equals("Horrizontaler Schwarz/Weiss Verlauf")) {
			generateCourseHorizonSW(width, height, pixels);
		}

		if (choice.equals("Vertikaler Schwarz/Weiss/Schwarz Verlauf")) {
			generateCourseVerticalSWS(width, height, pixels);
		}

		if (choice.equals("Horrizontaler Schwarz/Weiss/Schwarz Verlauf")) {
			generateCourseHorrizontalSWS(width, height, pixels);
		}

		if (choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf")) {
			generateMultiCourse(width, height, pixels);
		}

		if (choice.equals("Bahamische Flagge")) {
			generateBahamianFlag(width, height, pixels);
		}

		if (choice.equals("Tschechiche Flagge")) {
			generateTschechianFlag(width, height, pixels);
		}

		if (choice.equals("Bangladeschische Flagge")) {
			generateBangladeshianFlag(width, height, pixels);
		}

		if (choice.equals("Japanische Flagge")) {
			generateJapanFlag(width, height, pixels);
		}

		if (choice.equals("Japanische Flagge mit weichen Kanten")) {
			generateJapanFlagBlur(width, height, pixels);
		}

		if (choice.equals("Probeklausur Aufgabe 7")) {
			probeklausurAufgabe7(width, height, pixels);
		}

		////////////////////////////////////////////////////////////////

		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen

				int r = 0, g = 0, b = 0;
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateYellowImage(int width, int height, int[] pixels) {

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				int r = 255, g = 255, b = 0;

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateFrFlag(int width, int height, int[] pixels) {

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				int temp = width / 3;

				if (x <= temp) { // left
					int r = 0, g = 0, b = 255;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}

				if (x <= 2 * temp && x >= temp) { // middle
					int r = 255, g = 255, b = 255;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}

				if (x <= 3 * temp && x >= 2 * temp) { // right
					int r = 255, g = 0, b = 0;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
		}
	}

	private void generateCourseHorizonSW(int width, int height, int[] pixels) {

		float mx = (float) (255) / (float) (width); // constant for x growth

		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				int r = (int) (mx * x);
				int g = (int) (mx * x);
				int b = (int) (mx * x);

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	// Horiz. Schwarz-WeiÃŸ Verlauf
	private void generateCourseVerticalSW(int width, int height, int[] pixels) {

		float my = (float) (255) / (float) (height); // constant for y growth

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				int r = (int) (my * y);
				int g = (int) (my * y);
				int b = (int) (my * y);

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	// Horiz. Schwarz-WeiÃŸ-Schwarz Verlauf
	private void generateCourseVerticalSWS(int width, int height, int[] pixels) {

		int r = 0;
		int b = 0;
		int g = 0;
		float halfHeight = height / 2;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				if (y <= height / 2) {
					r = g = b = (int) ((float) y / halfHeight * 255);
					// System.out.println(r + " | " + g + " | " + b );
				}

				if (y > height / 2) {
					r = g = b = (int) ((float) ((height - y) / halfHeight * 255));
					// System.out.println(r + " | " + g + " | " + b );
				}

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	public void generateCourseHorrizontalSWS(int width, int height, int[] pixels) {
		float halfWidth = width / 2; // only taking the half of the screen

		for (int y = 0; y < height; y++) {
			// Schleife ueber die x-Werte
			for (int x = 0; x < width; x++) {
				int pos = y * width + x; // Arrayposition bestimmen

				int r = 0;
				int g = 0;
				int b = 0;

				// if the current pixel is at the first half
				if (x <= halfWidth) {
					r = g = b = (int) ((float) x / halfWidth * 255); // calculates the grade of grey by the position of
																		// the pixel
					// System.out.println(r + " " + g + " " + b);
				}

				// if the current pixel is at the second half
				if (x > halfWidth) {

					r = g = b = (int) ((float) ((width - x) / halfWidth * 255)); // calculates the grade of grey by the
																					// position of the pixel || value of
																					// the position must go down,
																					// because the grade should get
																					// darker
					// System.out.println(r + " " + g + " " + b);
				}

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	// horiz. Schwarz/Rot + vertik. Schwarz/Blau Verlauf
	private void generateMultiCourse(int width, int height, int[] pixels) {

		float mx = (float) (255) / (float) (width); // constant for x growth
		float my = (float) (255) / (float) (height); // constant for y growth

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				int r = (int) (mx * x);
				int g = 0;
				int b = (int) (my * y);

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}

	private void generateBahamianFlag(int width, int height, int[] pixels) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				int r = 0, g = 119, b = 139;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;

				// middle part
				if (y <= 2 * (height / 3) && y >= (height / 3)) {
					r = 255;
					g = 199;
					b = 44;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}

				// triangle
				if ((y <= height / 2 && x <= y) || (y >= height / 2 && x <= (height - y))) {
					r = 0;
					g = 0;
					b = 0;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
		}
	}

	private void generateTschechianFlag(int width, int height, int[] pixels) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				float tri = 0.75f;
				int r = 255, g = 255, b = 255;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;

				// lower part
				if (y >= (height / 2)) {
					r = 255;
					g = 0;
					b = 0;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}

				// triangle
				if ((y <= height / 2 && tri * x <= y) || (y >= height / 2 && tri * x <= (height - y))) {
					r = 17;
					g = 69;
					b = 126;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
		}
	}

	private void generateBangladeshianFlag(int width, int height, int[] pixels) {

		double distance; // between 2 points
		double radius = width / 5; // circle radius
		double xM = (double) width / 2.25; // x of center
		double yM = (double) height / 2; // y of center

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				// Pythagoras: c = sqrt(a^2 + b^2) a = (xM-x) b = (y-yM)
				distance = Math.sqrt((xM - x) * (xM - x) + (y - yM) * (y - yM));
				if (distance < radius) {
					int r = 244;
					int g = 42;
					int b = 65;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				} else {
					int r = 0;
					int g = 106;
					int b = 78;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
		}
	}

	private void generateJapanFlag(int width, int height, int[] pixels) {

		double distance; // between 2 points
		double radius = width / 5; // circle radius
		double xM = (double) width / 2; // x of center
		double yM = (double) height / 2; // y of center

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				distance = Math.sqrt((xM - x) * (xM - x) + (y - yM) * (y - yM));
				if (distance < radius) {
					int r = 255;
					int g = 0;
					int b = 0;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				} else {
					int r = 255;
					int g = 255;
					int b = 255;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
		}
	}

	private void generateJapanFlagBlur(int width, int height, int[] pixels) {
		generateJapanFlag(width, height, pixels);

		double distance; // between 2 points
		double radius = width / 5; // circle radius
		double xM = (double) width / 2; // x of center
		double yM = (double) height / 2; // y of center
		double blur = 0.8; // < 2
		int r, g, b;

		r = 255;
		g = 0;
		b = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;

				distance = Math.sqrt((xM - x) * (xM - x) + (y - yM) * (y - yM));

				if (distance <= radius) {
					// double temp = distance / ((xM - x) * (xM - x) + (y - yM) * (y - yM));
					// r = (int) (255 / temp);
					int td = (int) (distance * blur);
					// r == r;
					g = +td;
					b = +td;

					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
			}
		}
	}

	private void probeklausurAufgabe7(int width, int height, int[] pixels) {
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int pos = y * width + x;
				int r, g, b;
				
				// progress has to increase in double speed, because it only goes
				// from first quarter to the third one (so half of the width)
				double progress = (double) 2 * x / width;

				if (x <= width / 4) {
					r = g = 255;
					b = 0;
				} else if (x <= width / 4 * 3) {
					r = g = (int) ((1.5 - progress) * 255);
					b = (int) ((progress - 0.5) * 255);
				} else {
					r = g = 0;
					b = 255;
				}

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
	
	// von Pr�fungsvorbereitung mit Herrn Prof. Bartel, funktioniert aber noch nicht^^
	private void probeklausurAufgabe7error(int width, int height, int[] pixels) { 
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pos = y * width + x;
				
				int b = (x-width/4) * 255/width/2;
				int g = 255-b;
				int r = g;
				
				if(b < 0) {
					b = 0;
					r = g = 255;
				}
				if (b > 255) {
					b = 255;
					r = g = 0;
				}	
				
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
	

	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");

		gd.addChoice("Bildtyp", choices, choices[0]);

		gd.showDialog(); // generiere Eingabefenster

		choice = gd.getNextChoice(); // Auswahl uebernehmen

		if (gd.wasCanceled())
			System.exit(0);
	}
}
