package de.tu_chemnitz.mi.barcd.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An sequence of URLs determined by a template string. The template string
 * contains a tag which will be substituted for an integer in the given range.
 * The integer can be padded with zeros to a certain length.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class TemplatedURLSequence implements Iterable<URL> {
    private URL template;
    
    private String tag;

    private Range range;
    
    private int padding;
    
    /**
     * The format string used to construct the filenames {@link String#format}.
     */
    private String format;
    
    /**
     * @param template
     * @param tag
     * @param range
     * @param padding
     * 
     * @throws MalformedURLException if the URL is not valid
     */
    public TemplatedURLSequence(URL template, String tag, Range range, int padding)
    {
        this.template = template;
        this.tag = tag;
        this.padding = padding;
        this.range = range;
        
        if (padding < 0) {
            throw new IllegalArgumentException("padding must be non-negative");
        }
        
        String templateStr = template.toString();
        
        int tagPos = templateStr.indexOf(tag);
        if (tagPos < 0) {
            throw new IllegalArgumentException("pattern must contain the placeholder");
        }
        
        int q = templateStr.indexOf(tag, tagPos + tag.length());
        if (q > -1) {
            throw new IllegalArgumentException("pattern must contain the placeholder exactly once");
        }
        
        String before = templateStr.substring(0, tagPos);
        String after = templateStr.substring(tagPos + tag.length(), templateStr.length());
        
        if (padding == 0) {
            format = String.format("%s%%d%s", before, after);
        } else {
            format = String.format("%s%%0%dd%s", before, padding, after);
        }
    }
    
    public Range getRange() {
        return range;
    }
    
    public URL getTemplate() {
        return template;
    }
    
    public String getTag() {
        return tag;
    }
    
    public int getPadding() {
        return padding;
    }
    
    public URL getURL(int index)
        throws MalformedURLException
    {
        return new URL(String.format(format, range.get(index)));
    }
    
    @Override
    public TemplatedURLSequenceIterator iterator() {
        return new TemplatedURLSequenceIterator(this);
    }
}
