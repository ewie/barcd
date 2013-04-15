package de.tu_chemnitz.mi.barcd.video;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class FrameReaderException extends Exception {
    private static final long serialVersionUID = 1L;

    public FrameReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameReaderException(String message) {
        this(message, null);
    }

    public FrameReaderException(Throwable cause) {
        this(null, cause);
    }

    public FrameReaderException() {
        this(null, null);
    }
}