package de.tu_chemnitz.mi.barcd.image.lum;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public interface LuminanceImage {
    public static final int MIN_VALUE = Byte.MIN_VALUE;

    public static final int MAX_VALUE = Byte.MAX_VALUE;

    public static final int MIN_INTENSITY = 0x00;

    public static final int MAX_INTENSITY = 0xff;

    public int getWidth();

    public int getHeight();

    public int getIntensityAt(int x, int y);

    public void setIntensityAt(int x, int y, int v);

    public int getValueAt(int x, int y);

    public void setValueAt(int x, int y, int v);
}
