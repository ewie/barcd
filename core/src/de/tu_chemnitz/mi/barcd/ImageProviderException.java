package de.tu_chemnitz.mi.barcd;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class ImageProviderException extends Exception {
    private static final long serialVersionUID = 1L;

    public ImageProviderException() {
        super();
    }

    public ImageProviderException(String message) {
        super(message);
    }

    public ImageProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageProviderException(Throwable cause) {
        super(cause);
    }
}
