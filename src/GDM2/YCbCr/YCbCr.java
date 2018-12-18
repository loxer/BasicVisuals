package GDM2.YCbCr;

/**
 * Class for YCbCR-Colorspace and image processing
 * @author Sebastian Pütz
 * @version 2018-05-17
 */
public class YCbCr {
    private double y;
    private double cb;
    private double cr;

    public YCbCr() {
        // default constructor not in use
        y = 0;		cb = 0;		cr = 0;
    }

    public YCbCr(double y, double cb, double cr) {
        this.setY(y);
        this.setCb(cb);
        this.setCr(cr);
    }

    /**
     * Processes a change of brightness
     * @param value
     * @return
     */
    public YCbCr changeBrightness(double value) {
        YCbCr colorspace = new YCbCr();

        colorspace.setY(getY() + value);
        colorspace.setCb(getCb());
        colorspace.setCr(getCr());
        return colorspace;
    }


    /**
     * Processes a change of contrast
     * @param value
     * @return
     */
    public YCbCr changeContrast(double value) {
        YCbCr colorspace = new YCbCr();
        if(value != 0) colorspace.setY(value * (getY() - 128) + 128);
        else { colorspace.setY(getY());}
        colorspace.setCb(getCb());
        colorspace.setCr(getCr());
        return colorspace;
    }

    /**
     * Processes a change of saturation
     * @param value
     * @return
     */
    public YCbCr changeSaturation(double value) {
        YCbCr colorspace = new YCbCr();
        colorspace.setY(getY());

        colorspace.setCb(getCb() * value);
        colorspace.setCr(getCr() * value);
        return colorspace;
    }

    /**
     * Processes a change of hue
     * @param value
     * @return
     */
    public YCbCr changeHue(double value) {
        YCbCr colorspace = new YCbCr();
        colorspace.setY(getY());

        colorspace.setCb(getCb() * (Math.cos(Math.toRadians(value)) + Math.sin(Math.toRadians(value))));
        colorspace.setCr(getCr() * (Math.sin(Math.toRadians(value)) - Math.cos(Math.toRadians(value))));
        return colorspace;
    }

    public RGB transformToRGB() {
        double valueR = getY() + 1.402 * getCr();
        double valueG = getY() - 0.3441 * getCb() - 0.7141 * getCr();
        double valueB = getY() + 1.772 * getCb();

//		if(ImageEditor.DEBUG) {
//			System.out.println("R = " + getY() + " + 1.402 * " + getCr() + " = " + valueR);
//			System.out.println("G = " + getY() + " - 0.3441 * " + getCb() + "- 0.7141 * " + getCr() + " = " + valueG);
//			System.out.println("B = " + getY() + " + 1.772 * " + getCb() + " = " + valueB + "\n");
//			}

        return new RGB(valueR, valueG, valueB);
    }

    /**
     * Compares if values are equal
     * @param tmp
     * @return
     */
    public boolean equals(YCbCr tmp) {
        if (getY() == tmp.getY())
            if (getCb() == tmp.getCb())
                if (getCr() == tmp.getCr())

                    return true;

        return false;
    }

    // Accessors
    public double getY() {
        return y;
    }

    public double getCb() {
        return cb;
    }

    public double getCr() {
        return cr;
    }

    // Mutators
    public void setY(double y) {
        this.y = y;
    }

    public void setCb(double cb) {
        this.cb = cb;
    }

    public void setCr(double cr) {
        this.cr = cr;
    }
}
