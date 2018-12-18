package GDM2.YCbCr;

//ImageJ API
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.process.ImageProcessor;

import java.awt.Font;
import java.awt.Panel;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JSlider;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Opens an image window and adds a panel below the image
 */
public class ImageEditorYCbCr implements PlugIn {

    // DebugMode: True -> showing calculations
    final static boolean DEBUG = true;
    ImagePlus imp; // ImagePlus object
    private int[] orgPix;
    private int width;
    private int height;

    public static void main(String args[]) {
        IJ.open("E:\\workspace-ecl\\VisualComputing\\Files\\orchid.jpg");
        ImageEditorYCbCr pw = new ImageEditorYCbCr();

        if(DEBUG) RGB.testTransform(123, 123, 123);

        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp == null) imp = WindowManager.getCurrentImage();
        if (imp == null) return;

        CustomCanvas cc = new CustomCanvas(imp);
        storePixelValues(imp.getProcessor());
        new CustomWindow(imp, cc);
    }

    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        orgPix = ((int[]) ip.getPixels()).clone();
    }

    // Class for creating an new canvas for images
    class CustomCanvas extends ImageCanvas {

        CustomCanvas(ImagePlus imp) {
            super(imp);
        }

    } // End class CustomCanvas


    // Class for creating an editor window
    class CustomWindow extends ImageWindow implements ChangeListener {

        private JSlider jSliderBrightness;
        private JSlider jSliderContrast;
        private JSlider jSliderSaturation;
        private JSlider jSliderHue;

        private double brightness;
        private double contrast;
        private double saturation;
        private double hue;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();

            // default values
            brightness = 0;
            contrast = 1;
            saturation = 1;
            hue = 90;
        }

        void addPanel() {
            Panel panel = new Panel();
            panel.setLayout(new GridLayout(4, 1));

            jSliderBrightness = makeTitledSilder("Brigthness: ", -128, 128, 0);
            jSliderContrast = makeTitledSilder("Contrast: ", 1, 10, 1);
            jSliderSaturation = makeTitledSilder("Saturation: ", 0, 10, 1);
            jSliderHue = makeTitledSilder("Hue: ", 0, 360, 90);

            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);

            add(panel);
            pack();
        }

        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {

            JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val);
            Dimension preferredSize = new Dimension(width, 50);
            slider.setPreferredSize(preferredSize);
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), string,
                    TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                    new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
            slider.setPaintTicks(true);
            slider.addChangeListener(this);
            slider.setMajorTickSpacing((maxVal - minVal) / 10);

            return slider;
        }

        private void setSliderTitle(JSlider slider, String str) {
            TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), str, TitledBorder.LEFT,
                    TitledBorder.ABOVE_BOTTOM, new Font("Sans", Font.PLAIN, 11));
            slider.setBorder(tb);
        }

        public void stateChanged(ChangeEvent e) {
            JSlider slider = (JSlider) e.getSource();

            if (slider == jSliderBrightness) {
                brightness = slider.getValue();
                String str = "Brightness " + brightness;
                setSliderTitle(jSliderBrightness, str);
            }

            if (slider == jSliderContrast) {
                contrast = slider.getValue();
                String str = "Contrast " + contrast;
                setSliderTitle(jSliderContrast, str);
            }

            if (slider == jSliderSaturation) {
                saturation = slider.getValue();
                String str = "Saturation " + saturation;
                setSliderTitle(jSliderSaturation, str);
            }

            if (slider == jSliderHue) {
                hue = slider.getValue();
                String str = "Hue " + hue;
                setSliderTitle(jSliderHue, str);
            }

            changePixelValues(imp.getProcessor());
            imp.updateAndDraw();
        }

        private void changePixelValues(ImageProcessor ip) {

            // Array Acces for pixel values
            int[] pixels = (int[]) ip.getPixels();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pos = y * width + x;
                    // accessing org. values
                    int argb = orgPix[pos];

                    int r = (argb >> 16) & 0xff;
                    int g = (argb >> 8) & 0xff;
                    int b = argb & 0xff;

                    // Before values -> function
                    RGB inRGB = new RGB(r, g, b);
                    YCbCr ycbcr = inRGB.transformToYCbCr();
                    ycbcr = ycbcr.changeBrightness(brightness);
                    ycbcr = ycbcr.changeContrast(contrast);
                    ycbcr = ycbcr.changeSaturation(saturation);
                    ycbcr = ycbcr.changeHue(hue);

                    // Valuecheck in class RGB
                    RGB outRGB = ycbcr.transformToRGB();

                    // After values -> outRGB
                    pixels[pos] = (0xFF<<24) | (outRGB.getR()<<16) | (outRGB.getG()<<8) | outRGB.getB();
                }
            }
        }
    } // End of class CustomWindow
}