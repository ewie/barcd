package de.tu_chemnitz.mi.barcd.image;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface GlobalThresholdSelector {
    public int getThreshold(LuminanceImage source);
}
