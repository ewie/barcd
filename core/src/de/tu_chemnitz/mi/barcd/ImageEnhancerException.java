package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageEnhancerException extends Exception {
    private static final long serialVersionUID = 1L;

    public ImageEnhancerException() {
        super();
    }

    public ImageEnhancerException(String message) {
        super(message);
    }

    public ImageEnhancerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageEnhancerException(Throwable cause) {
        super(cause);
    }
}
