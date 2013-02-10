package de.tu_chemnitz.mi.barcd.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class TemplatedURLSequenceIterator implements Iterator<URL> {
    private TemplatedURLSequence template;
    
    private int index = 0;
    
    public TemplatedURLSequenceIterator(TemplatedURLSequence pattern) {
        this.template = pattern;
    }

    @Override
    public boolean hasNext() {
        return index < template.getRange().getLength();
    }

    @Override
    public URL next() {
        try {
            return template.getURL(index++);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
