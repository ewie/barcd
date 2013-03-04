package de.tu_chemnitz.mi.barcd.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * An {@link Iterator} over the URLs provided by a {@link TemplatedURLSequence}.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class TemplatedURLSequenceIterator implements Iterator<URL> {
    private TemplatedURLSequence template;

    private int index = 0;

    /**
     * @param sequence the templated URL sequence
     */
    public TemplatedURLSequenceIterator(TemplatedURLSequence sequence) {
        template = sequence;
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
