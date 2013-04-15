package de.tu_chemnitz.mi.barcd.video;

import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * A device (e.g. webcam) based frame reader using OpenCV.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class OpenCvDeviceFrameReader extends OpenCvFrameReader {
    private final int deviceNumber;

    protected OpenCvDeviceFrameReader(int deviceId, OpenCVFrameGrabber frameGrabber)
        throws FrameReaderException
    {
        super(frameGrabber);
        deviceNumber = deviceId;
    }

    /**
     * @return the number of the device this frame reader is using
     */
    public int getDeviceNumber() {
        return deviceNumber;
    }

    /**
     * Create a frame reader using a device.
     *
     * @param deviceNumber the number denoting the device
     *                     (0 = 1st device, 1 = 2nd device, etc.)
     *
     * @return the frame reader using the specified device
     *
     * @throws FrameReaderException
     */
    public static OpenCvDeviceFrameReader open(int deviceNumber)
        throws FrameReaderException
    {
        OpenCVFrameGrabber fg = new OpenCVFrameGrabber(deviceNumber);
        return new OpenCvDeviceFrameReader(deviceNumber, fg);
    }

    @Override
    public boolean hasMoreFrames() {
        return true;
    }
}
