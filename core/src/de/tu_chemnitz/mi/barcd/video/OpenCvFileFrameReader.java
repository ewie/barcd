/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.video;

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
}
