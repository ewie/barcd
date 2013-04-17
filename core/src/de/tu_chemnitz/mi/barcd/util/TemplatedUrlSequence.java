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
public class TemplatedUrlSequence implements Iterable<URL> {
    private final URL template;

    private final String tag;

    private final Range range;

    private final int padding;

    /**
     * The format string used to construct the URLs {@link String#format}.
     */
    private final String format;

    /**
     * @param template the URL template
     * @param tag the template tag, a unique substring in the URL template
     * @param range the range serving the sequence of integers substituting the
     *   template tag
     * @param padding the minimum number of characters used to represent the
     *   sequence number (will be padded with '0' to the left), use 0 for no
     *   padding at all
     */
    public TemplatedUrlSequence(URL template, String tag, Range range, int padding)
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

    /**
     * @return the range used for the sequence
     */
    public Range getRange() {
        return range;
    }

    /**
     * @return the URL template
     */
    public URL getTemplate() {
        return template;
    }

    /**
     * @return the template tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return the padding length
     */
    public int getPadding() {
        return padding;
    }

    /**
     * Construct the URL at the given index.
     *
     * @param index the zero-based index into the sequence
     *
     * @return the URL for the given index
     *
     * @throws MalformedURLException if the URL is not valid
     *   (This should actually not be the case because the tag will just be
     *   substituted for an integer causing a valid URL if the URL template is
     *   a valid URL.)
     */
    public URL getUrl(int index)
        throws MalformedURLException
    {
        return new URL(String.format(format, range.get(index)));
    }

    /**
     * The iterator over all possible URLs of this template.
     */
    @Override
    public TemplatedUrlSequenceIterator iterator() {
        return new TemplatedUrlSequenceIterator(this);
    }
}
