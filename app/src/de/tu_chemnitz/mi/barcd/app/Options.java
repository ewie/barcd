package de.tu_chemnitz.mi.barcd.app;

import java.io.File;
import java.net.URL;

import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequence;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Options {
    private File jobFile;
    private TemplatedUrlSequence frameUrlSequence;
    private URL xmlSchemaUrl;
    private boolean display;
    private boolean persist;

    public void setJobFile(File value) {
        jobFile = value;
    }

    public File getJobFile() {
        return jobFile;
    }

    public void setFrameUrlSequence(TemplatedUrlSequence value) {
        frameUrlSequence = value;
    }

    public TemplatedUrlSequence getFrameUrlSequence() {
        return frameUrlSequence;
    }

    public void setXmlSchemaUrl(URL value) {
        xmlSchemaUrl = value;
    }

    public URL getXmlSchemaUrl() {
        return xmlSchemaUrl;
    }

    public void setDisplay(boolean value) {
        display = value;
    }

    public boolean getDisplay() {
        return display;
    }

    public void setPersist(boolean value) {
        persist = value;
    }

    public boolean getPersist() {
        return persist;
    }
}
