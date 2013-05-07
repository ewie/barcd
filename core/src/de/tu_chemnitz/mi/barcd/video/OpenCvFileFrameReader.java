/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.video;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * A file based seekable frame reader using OpenCV.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class OpenCvFileFrameReader extends OpenCvFrameReader
    implements SeekableFrameReader
{
    private final URL url;

    protected OpenCvFileFrameReader(URL url, OpenCVFrameGrabber frameGrabber)
        throws FrameReaderException
    {
        super(frameGrabber);
        this.url = url;
    }

    /**
     * @return the URL of the file this frame reader is using
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Open a local or remote resource.
     *
     * @param url the resource URL
     *
     * @return the frame reader using the specified resource
     *
     * @throws FrameReaderException
     */
    public static OpenCvFileFrameReader open(URL url)
        throws FrameReaderException
    {
        // Check if the resource given by its URL may exist, because a
        // non-existent resource (remote or local) would cause a fatal runtime
        // error probably caused by JavaCV.
        if (!resourceMayExist(url)) {
            throw new FrameReaderException("resource does not exist");
        }
        OpenCVFrameGrabber fg = new OpenCVFrameGrabber(url.toString());
        return new OpenCvFileFrameReader(url, fg);
    }

    @Override
    public boolean hasMoreFrames() {
        return getCurrentFrameNumber() < getLengthInFrames();
    }

    @Override
    public void setFrameNumber(int frameNumber)
        throws FrameReaderException
    {
        try {
            frameGrabber.setFrameNumber(frameNumber);
        } catch (Exception ex) {
            throw new FrameReaderException(ex);
        }
    }

    @Override
    public void skipFrames(int count)
        throws FrameReaderException
    {
        setFrameNumber(getCurrentFrameNumber() + count);
    }

    @Override
    public int getLengthInFrames() {
        return frameGrabber.getLengthInFrames();
    }

    /**
     * Check if the resource given by its URL may exist.
     *
     * @param url
     *
     * @return true if the resource may exist, false if the resource definitely
     *   does not exist
     */
    private static boolean resourceMayExist(URL url) {
        String schema = url.getProtocol();
        if (schema.equals("http")) {
            return httpResourceMayExist(url);
        } else if (schema.equals("file")) {
            return resourceMayExistFile(url);
        }
        return true;
    }

    private static boolean httpResourceMayExist(URL url) {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            return true;
        }
        try {
            conn.setRequestMethod("HEAD");
        } catch (ProtocolException ex) {
            return true;
        }
        try {
            conn.connect();
        } catch (IOException ex) {
            return true;
        }
        try {
            return 200 == conn.getResponseCode();
        } catch (IOException ex) {
            return true;
        }
    }

    private static boolean resourceMayExistFile(URL url) {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException ex) {
            return false;
        }
        File file = new File(uri);
        return file.exists();
    }
}
