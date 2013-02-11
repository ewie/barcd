package de.tu_chemnitz.mi.barcd.video;

import java.net.URL;

import com.googlecode.javacv.OpenCVFrameGrabber;

public class OpenCVFileFrameReader extends OpenCVFrameReader {
    private URL url;
    
    protected OpenCVFileFrameReader(URL url, OpenCVFrameGrabber frameGrabber)
        throws FrameReaderException
    {
        super(frameGrabber);
        this.url = url;
    }

    /**
     * @return the URL of the file this frame reader is using
     */
    public URL getURL() {
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
    public static OpenCVFileFrameReader open(URL url)
        throws FrameReaderException
    {
        OpenCVFrameGrabber fg = new OpenCVFrameGrabber(url.toString());
        return new OpenCVFileFrameReader(url, fg);
    }

}
