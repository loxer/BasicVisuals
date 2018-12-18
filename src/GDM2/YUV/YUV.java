package GDM2.YUV;

/**
 * Class for YUV-Colorspace and image processing
 * @author Sebastian Pütz
 * @version 2018-05-17
 */
public class YUV {
    private double y;
    private double u;
    private double v;

    public YUV() {
        // default constructor not in use
        y = 0;		u = 0;		v = 0;
    }

    public YUV(double y, double u, double v) {
        this.setY(y);
        this.setU(u);
        this.setV(v);
    }

    /**
     * Processes a change of brightness	 *
     * @param value
     * @return
     */
    public YUV changeBrightness(double value) {
        YUV colorspace = new YUV();
        colorspace.setY(getY()+value);
        colorspace.setU(getU());
        colorspace.setV(getV());
        return colorspace;
    }

    /**
     * Processes a change of contrast
     *
     * @param value
     * @return
     */
    public YUV changeContrast(double value) {
        YUV colorspace = new YUV();

        //TODO #check mathematics
        colorspace.setY((getY()- 127)*value+127);
        colorspace.setU(getU());
        colorspace.setV(getV());
        return colorspace;
    }

    /**
     * Processes a change of saturation
     *
     * @param value
     * @return
     */
    public YUV changeSaturation(double value) {
        YUV colorspace = new YUV();

        //TODO #Done
        colorspace.setY(getY());
        colorspace.setU(getU() * value);
        colorspace.setV(getV() * value);
        return colorspace;
    }

    /**
     * Processes a change of hue
     *
     * @param value
     * @return
     */
    public YUV changeHue(double value) {
        YUV colorspace = new YUV();
        colorspace.setY(getY());

        //TODO #ceck mathematics
        colorspace.setU(getU() * (Math.cos(Math.toRadians(value)) + Math.sin(Math.toRadians(value))));
        colorspace.setV(getV() * (Math.sin(Math.toRadians(value)) - Math.cos(Math.toRadians(value))));
        return colorspace;
    }

    public RGB transformToRGB() {

        //TODO #check mathematics
        double valueR = getY() + getV() / 0.877;
        double valueB = getY() + getU()/0.493;
        double valueG = 1/0.587 * getY() - 0.299/0.587* valueR - 0.114/0.587 * valueB;

        return new RGB(valueR, valueG, valueB);
    }

    /**
     * Compares if values are equal
     *
     * @param tmp
     * @return
     */
    public boolean equals(YUV tmp) {
        if (getY() == tmp.getY())
            if (getU() == tmp.getU())
                if (getV() == tmp.getV())

                    return true;

        return false;
    }

    // Accessors
    public double getY() {
        return y;
    }

    public double getU() {
        return u;
    }

    public double getV() {
        return v;
    }

    // Mutators
    public void setY(double y) {
        this.y = y;
    }

    public void setU(double u) {
        this.u = u;
    }

    public void setV(double v) {
        this.v = v;
    }
}