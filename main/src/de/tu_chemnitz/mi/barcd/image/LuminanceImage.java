package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <erik.wienhold@informatik.tu-chemnitz.de>
 */
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
