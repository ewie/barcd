package de.tu_chemnitz.mi.barcd;

public class Extraction {
    public enum Type {
        CODE39,
        CODE128,
        EAN13,
        QR,
        UPCA,
        UNKNOWN
    }
    
    private Type type;
    private String text;
    private byte[] raw;
    
    public Extraction(Type type, String text, byte[] raw) {
        this.type = type;
        this.text = text;
        this.raw = raw;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public String getText() {
        return this.text;
    }
    
    public byte[] getRaw() {
        return this.raw;
    }
}
