package de.tu_chemnitz.mi.barcd.video;

import com.googlecode.javacv.OpenCVFrameGrabber;

public class OpenCVDeviceFrameReader extends OpenCVFrameReader {
    private int deviceNumber;
    
    protected OpenCVDeviceFrameReader(int deviceId, OpenCVFrameGrabber frameGrabber)
        throws FrameReaderException
    {
        super(frameGrabber);
        this.deviceNumber = deviceId;
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
    public static OpenCVDeviceFrameReader open(int deviceNumber)
        throws FrameReaderException
    {
        OpenCVFrameGrabber fg = new OpenCVFrameGrabber(deviceNumber);
        return new OpenCVDeviceFrameReader(deviceNumber, fg);
    }
}
