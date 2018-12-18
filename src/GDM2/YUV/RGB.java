package GDM2.YUV;

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
     * Transfers RGB -> YUV
     * @return new instance of YUV
     */
    public YUV transformToYUV() {

        // transformation
        double tmpY = 0.299 * getR() + 0.587 * getG() + 0.114 * getB();
        double tmpU = (getB()-tmpY)*0.493;
        double tmpV = (getR()-tmpY)*0.877;

        return new YUV(tmpY, tmpU, tmpV);
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
        YUV beforeYUV = beforeRGB.transformToYUV();

        // Atfer calculations
        RGB afterRGB = beforeYUV.transformToRGB();
        System.out.println("RGB after:\n" + afterRGB.toString() + "\n");
        YUV afterYUV = afterRGB.transformToYUV();

        // Evaluation of results
        System.out.println("RGB calculation is " + beforeRGB.equals(afterRGB) + "!");
        System.out.println("YUV calculation is "  + beforeYUV.equals(afterYUV) + "!");
        System.out.println("\n");
    }

    public boolean equals(RGB RGB) {
        if (getR() == RGB.getR()) {
            if (getG() == RGB.getG()) {
                if (getB() == RGB.getB()) {

                    return true;

                } else System.out.println("B " + getB() + " != " + RGB.getB());
            } else System.out.println("G " + getG() + " != " + RGB.getG());
        } else System.out.println("R = " + getR() + " != " + RGB.getR());

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