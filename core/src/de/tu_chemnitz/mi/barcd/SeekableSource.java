package de.tu_chemnitz.mi.barcd;

/**
 * Encapsulates information about a seekable image source.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class SeekableSource implements Source {
    private final int initialFrameNumber;
    
    public SeekableSource(int initialFrameNumber) {
        this.initialFrameNumber = initialFrameNumber;
    }

    public final int getInitialFrameNumber() {
        return initialFrameNumber;
    }
}
