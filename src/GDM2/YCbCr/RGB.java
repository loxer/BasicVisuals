package GDM2.YCbCr;

/**
 * Class for RGB-Colorspace
 * @author Sebastian Pütz
 * @version 2018-05-17
 */
public class RGB {
    private int r;
    private int g;
    private int b;

    public RGB(double r, double g, double b) {
        this.setR((int) Math.round(r));
        this.setG((int) Math.round(g));
        this.setB((int) Math.round(b));
    }

    public RGB(int r, int g, int b) {
        this.setR(r);
        this.setG(g);
        this.setB(b);
    }

    /**
     * Transfers RGB -> YCbCr
     * @return new instance of YCbCr
     */
    public YCbCr transformToYCbCr() {
        double tmpY = 0.299 * getR() + 0.587 * getG() + 0.114 * getB();
        double tmpCb = -0.168736 * getR() - 0.331264 * getG() + 0.5 * getB();
        double tmpCr = 0.5 * getR() - 0.418688 * getG() - 0.081312 * getB();

        return new YCbCr(tmpY, tmpCb, tmpCr);
    }

    /**
     * (Non-Javadoc) Overrides
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ("R = " + getR() + " | " + "G = " + getG() + " | " + "B = " + getB());
    }

    /**
     * Tests the transform calculations
     * @param r, g, b
     */
    public static void testTransform(int r, int g, int b) {

        // Before calculations
        RGB beforeRGB = new RGB(r, g, b);
        System.out.println("RGB before:\n" + beforeRGB.toString() + "\n");
        YCbCr beforeYCbCr = beforeRGB.transformToYCbCr();

        // Atfer calculations
        RGB afterRGB = beforeYCbCr.transformToRGB();
        System.out.println("RGB after:\n" + afterRGB.toString() + "\n");
        YCbCr afterYCbCr = afterRGB.transformToYCbCr();

        // Evaluation of results
        System.out.println("RGB calculation is " + beforeRGB.equals(afterRGB) + "!");
        System.out.println("YCbCR calculation is "  + beforeYCbCr.equals(afterYCbCr) + "!");
        System.out.println("\n");
    }

    public boolean equals(RGB rgb) {
        if (getR() == rgb.getR()) {
            if (getG() == rgb.getG()) {
                if (getB() == rgb.getB()) {

                    return true;

                } else System.out.println("B " + getB() + " != " + rgb.getB());
            } else System.out.println("G " + getG() + " != " + rgb.getG());
        } else System.out.println("R = " + getR() + " != " + rgb.getR());

        return false;
    }

    /**
     * If Overflow/Underflow -> repair it
     * @param x
     * @return
     */
    public int checkValues(int x) {
        int value = x;

        if (x < 0)  	value =   0;
        if (x > 255) 	value = 255;
        return value;
    }

    // Accesor
    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    // Mutator
    public void setR(int r) {
        // R -> checking values
        this.r = checkValues(r);

    }

    public void setG(int g) {
        // G -> checking values
        this.g = checkValues(g);

    }

    public void setB(int b) {
        // B -> checking values
        this.b = checkValues(b);
    }
}