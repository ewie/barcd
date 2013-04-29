package de.tu_chemnitz.mi.barcd.xml;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class XmlSerializerValidationError {
    private final String message;
    private final int line;
    private final int column;

    public XmlSerializerValidationError(String message, int line, int column) {
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
