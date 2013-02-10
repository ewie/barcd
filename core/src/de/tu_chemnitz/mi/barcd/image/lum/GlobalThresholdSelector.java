package de.tu_chemnitz.mi.barcd.image.lum;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
@Deprecated
public interface GlobalThresholdSelector {
    public int getThreshold(LuminanceImage source);
}
