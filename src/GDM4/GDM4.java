package GDM4;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;

public class GDM4 implements PlugInFilter {

    protected ImagePlus imp;
    final static String[] choices = { "Wischen", "Weiche Blende", "Overlay", "Schieb-Blende", "Chroma Key", "Extra" };

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB + STACK_REQUIRED;
    }

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

//        IJ.open("E:\\workspace\\BasicVisuals\\Files\\StackB.zip");
      IJ.open("C:\\Users\\loxer\\Dropbox\\Eigene Dateien\\Documents\\Studium\\Grundlagen digitaler Medien\\Fiji.app\\images\\StackB.zip");

        GDM4 sd = new GDM4();
        sd.imp = IJ.getImage();
        ImageProcessor B_ip = sd.imp.getProcessor();
        sd.run(B_ip);
    }

    public void run(ImageProcessor B_ip) {
        // Film B wird uebergeben
        ImageStack stack_B = imp.getStack();

        int length = stack_B.getSize();
        int width = B_ip.getWidth();
        int height = B_ip.getHeight();

        // ermoeglicht das Laden eines Bildes / Films
        Opener o = new Opener();
        OpenDialog od_A = new OpenDialog("Auswaehlen des 2. Filmes ...", "");

        // Film A wird dazugeladen
        String dateiA = od_A.getFileName();
        if (dateiA == null)
            return; // Abbruch
        String pfadA = od_A.getDirectory();
        ImagePlus A = o.openImage(pfadA, dateiA);
        if (A == null)
            return; // Abbruch

        ImageProcessor A_ip = A.getProcessor();
        ImageStack stack_A = A.getStack();

        if (A_ip.getWidth() != width || A_ip.getHeight() != height) {
            IJ.showMessage("Fehler", "Bildgroessen passen nicht zusammen");
            return;
        }

        // Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
        length = Math.min(length, stack_A.getSize());

        ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
        ImageStack stack_Erg = Erg.getStack();

        // Dialog fuer Auswahl des Ueberlagerungsmodus
        GenericDialog gd = new GenericDialog("Ueberlagerung");
        gd.addChoice("Methode", choices, "");
        gd.showDialog();

        int methode = 0;
        String s = gd.getNextChoice();
        if (s.equals("Wischen"))
            methode = 1;
        if (s.equals("Weiche Blende"))
            methode = 2;
        if (s.equals("Overlay"))
            methode = 3;
        if (s.equals("Schieb-Blende"))
            methode = 4;
        if (s.equals("Chroma Key"))
            methode = 5;
        if (s.equals("Extra"))
            methode = 6;

        // Arrays fuer die einzelnen Bilder
        int[] pixels_B;
        int[] pixels_A;
        int[] pixels_Erg;

        // Schleife ueber alle Bilder
        for (int z = 1; z <= length; z++) {
            pixels_B = (int[]) stack_B.getPixels(z);
            pixels_A = (int[]) stack_A.getPixels(z);
            pixels_Erg = (int[]) stack_Erg.getPixels(z);

            double progress = (double) z / length;
            int r, g, b;
            int pos = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++, pos++) {

                    int cA = pixels_A[pos];
                    int rA = (cA & 0xff0000) >> 16;
                    int gA = (cA & 0x00ff00) >> 8;
                    int bA = (cA & 0x0000ff);

                    int cB = pixels_B[pos];
                    int rB = (cB & 0xff0000) >> 16;
                    int gB = (cB & 0x00ff00) >> 8;
                    int bB = (cB & 0x0000ff);

                    if (methode == 1) // Wischen
                    {
                        if (y + 1 > (z - 1) * (double) height / (length - 1))
                            pixels_Erg[pos] = pixels_B[pos];
                        else
                            pixels_Erg[pos] = pixels_A[pos];
                    }

                    if (methode == 2) // Weiche Blende
                    {
                        r = (int) ((1 - progress) * rB + progress * rA);
                        g = (int) ((1 - progress) * gB + progress * gA);
                        b = (int) ((1 - progress) * bB + progress * bA);

                        // if(x == 1 && y == 1) {
                        // System.out.println("r: " + r + " | rA: " + rA + " | rB: " + rB + " |
                        // progress: " + progress + " | z: " + z + " | length: " + length);
                        // }

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
                    }

                    if (methode == 3) // Overlay
                    {   //TODO richtiges Overlay (Subtraktiv/Additiv)
                        r = (rA + rB) / 2;
                        g = (gA + gB) / 2;
                        b = (bA + bB) / 2;

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
                    }

                    if (methode == 4) // Schieb-Blende
                    {
                        int temp = (int) (width * progress);

                        if (x >= temp)
                            // Wo befindet sich der aktuelle Pixel im Verhältnis zur Zeit?
                            // Je weiter rechts und je weiter links er ist, desto eher wird die else-Schleife ausgeführt
                        {
                            pixels_Erg[pos] = pixels_A[pos - temp];
                            // Das erste Bild wird nach rechts geschoben, der Pixel im neuen Bild ist im BildA
                            // weiter links gewesen
                        } else {
                            pixels_Erg[pos] = pixels_B[(int) pos + (width - temp)];
                            // je weiter der Progress vorangeschritten ist, desto weiter wandern die Pixel nach rechts
                        }
                    }

                    if (methode == 5) // Chroma Key
                    {
                        int radiusToAvoid = 120;
                        int[] pixelToAvoid = {170, 120, 100};
                        double vectorFromCurrentPixelToPixelToAvoid;

                        vectorFromCurrentPixelToPixelToAvoid = Math.sqrt(Math.pow(rA - pixelToAvoid[0], 2)
                                + Math.pow(gA - pixelToAvoid[1], 2) + Math.pow(bA - pixelToAvoid[2], 2));
                        if (vectorFromCurrentPixelToPixelToAvoid < radiusToAvoid) {
                            r = rB;
                            g = gB;
                            b = bB;
                        }
                        else {
                            r = rA;
                            g = gA;
                            b = bA;
                        }

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
                    }

                    if (methode == 6) // Extra
                    {
//						int edge1Weight = 80;
//						int edge1Height = 0 + 2 * z;
//
//						int edge2Weight = 220;
//						int edge2Height = 80 + 2 * z;

                        // Hinweis: z startet bei 1, nicht bei 0!!!

                        int square1Edge1AtWeight = 80 - 2 * z;
                        int square1Edge1AtHeight = 0;

                        int square1Edge2AtWeight = 150 - 2 * z;
                        int square1Edge2AtHeight = 80;

                        int square2Edge1AtWeight = 149;
                        int square2Edge1AtHeight = -2 + 2 * z;

                        int square2Edge2AtWeight = 220;
                        int square2Edge2AtHeight = 78 + 2 * z;


                        if ((x >= square1Edge1AtWeight && y >= square1Edge1AtHeight && x <= square1Edge2AtWeight && y <= square1Edge2AtHeight) ||
                                (x >= square2Edge1AtWeight && y >= square2Edge1AtHeight && x <= square2Edge2AtWeight && y <= square2Edge2AtHeight)) {
                            r = rB;
                            g = gB;
                            b = bB;
                        } else {
                            r = rA;
                            g = gA;
                            b = bA;
                        }
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
                    }
                }
            }
        }

        // neues Bild anzeigen
        Erg.show();
        Erg.updateAndDraw();
    }
}