package de.tu_chemnitz.mi.barcd.xml;

public class XMLSerializerValidationError {
    private String message;
    private int line;
    private int column;
    
    public XMLSerializerValidationError(String message, int line, int column) {
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
