package GDM3;

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
 Opens an image window and adds a panel below the image
 */
public class GDM3 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = 	{"Original", "Red-Channel", "Green-Channel", "Blue-Channel", "Negative",
            "Greylevel", "Binear", "Binear Errordithering", "Sepia", "Colorlevel"};


    public static void main(String args[]) {

        IJ.open("E:\\workspace\\BasicVisuals\\Files\\Bear.jpg");
//		IJ.open("C:\\Users\\loxer\\Dropbox\\Eigene Dateien\\Documents\\Studium\\Grundlagen digitaler Medien\\Übung\\3\\Bear.jpg"); //Flo

        GDM3 pw = new GDM3();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp==null)
            imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);
        storePixelValues(imp.getProcessor());
        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int []) ip.getPixels()).clone();
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
            //JPanel panel = new JPanel();
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
            // Array to save pixel values
            int[] pixels = (int[])ip.getPixels();

            if (method.equals("Original")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Red-Channel")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
//						int g = (argb >>  8) & 0xff;
//						int b =  argb        & 0xff;

                        int rn = r;
                        int gn = 0;
                        int bn = 0;

                        // Check pixel value boundary

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Green-Channel")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int g = (argb >>  8) & 0xff;

                        int rn = 0;
                        int gn = g;
                        int bn = 0;

                        // Check pixel value boundary

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Blue-Channel")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int b =  argb        & 0xff;

                        int rn = 0;
                        int gn = 0;
                        int bn = b;

                        // Check pixel value boundary

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Negative")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        // Easy to negate
                        int rn = 255-r;
                        int gn = 255-g;
                        int bn = 255-b;

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Greylevel")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = (r+g+b)/3;
                        int gn = rn;
                        int bn = rn;

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Binear")) {
                //TODO Binear

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn, gn, bn;

                        if((r+g+b)/3 < 128) {
                            rn = gn = bn = 0;
                        }
                        else {rn = gn = bn = 255;}

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Binear Errordithering")) {
                int ditheringValue = 0;

                for (int y=0; y<height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn, gn, bn;
                        int brightnessAndDithering = ((r + g + b) / 3) + ditheringValue;

                        if (brightnessAndDithering < 128) {
                            rn = gn = bn = 0;
                            ditheringValue = brightnessAndDithering;
                            // brightness too low -> therefore the ditheringValue must be positiv
                        } else {
                            rn = gn = bn = 255;
                            ditheringValue = brightnessAndDithering - 255;
                            // brightness too high -> ditheringValue must be negativ
                        }
                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Sepia")) {
                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        // greylevel
                        int rn = (r+g+b)/3;
                        int gn = rn;
                        int bn = rn;

                        rn = (int) (1*rn);
                        gn = (int) (0.65*gn);
                        bn = (int) (0.35*bn);

//						LSD-Effekt
//						rn = (int) (1.33*r + 1.33*g + 1.33*b);
//						gn = g;
//						bn = (int) (0.66*r + 0.66*g + 0.66*b);



                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Colorlevel")) {

                int[][] availableColors = {
                        {230, 230, 230},
                        { 15,  15,  15},
                        {100,  90,  85},
                        { 25,  94, 117},
                        {187, 137, 111},
                        {145, 108,  79},
//						{122, 122, 114}
                };

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int[] originalColor = {r, g, b};
                        int newColor = chooseColor(availableColors, originalColor);

                        int rn = availableColors[newColor][0];
                        int gn = availableColors[newColor][1];
                        int bn = availableColors[newColor][2];

//						if(x == 200 && y == 200) {
//						System.out.println(availableColors.length);
//						System.out.println(availableColors[0].length);
//						System.out.println("Red: " + r + " | Green: " + g + " | Blue: " + b);
//						System.out.println("Red: " + rn + " | Green: " + gn + " | Blue: " + bn);
//						}

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }
        }

        private int chooseColor(int[][] availableColors, int[] originalColor) {
            int closestColor = 0;
            double shortestColorVector = 999999999;

            // one round for each color
            for(int currentColor = 0; currentColor < availableColors.length; currentColor++) {
                double lengthCalculation = 0;

                // 3 rounds in RGB
                for(int currentChroma = 0; currentChroma < availableColors[currentColor].length; currentChroma++) {

                    // Calculating the length of the vector to each possible color => lengthCalculation = (r^2+b^2+g^2)
                    lengthCalculation = lengthCalculation + Math.pow((availableColors[currentColor][currentChroma] - originalColor[currentChroma]), 2);
                }

                // Compare to shortest vector
                if(lengthCalculation < shortestColorVector) {
                    shortestColorVector = lengthCalculation;
                    closestColor = currentColor;
                }
            }
            return closestColor;
        }
    } // CustomWindow inner class
}