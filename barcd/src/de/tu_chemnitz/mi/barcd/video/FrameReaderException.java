package de.tu_chemnitz.mi.barcd.video;

public class FrameReaderException extends Exception {
    private static final long serialVersionUID = 1L;

    public FrameReaderException() {
        super();
    }
    
    public FrameReaderException(String message) {
        super(message);
    }
    
    public FrameReaderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FrameReaderException(Throwable cause) {
        super(cause);
    }
}