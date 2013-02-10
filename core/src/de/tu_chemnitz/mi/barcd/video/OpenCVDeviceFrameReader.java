package de.tu_chemnitz.mi.barcd.video;

import com.googlecode.javacv.OpenCVFrameGrabber;

public class OpenCVDeviceFrameReader extends OpenCVFrameReader {
    private int deviceId;
    
    protected OpenCVDeviceFrameReader(int deviceId, OpenCVFrameGrabber frameGrabber)
        throws FrameReaderException
    {
        super(frameGrabber);
        this.deviceId = deviceId;
    }

    /**
     * @return the ID of the device this frame reader is using
     */
    public Object getDeviceId() {
        return deviceId;
    }

    /**
     * Create a frame reader using a device.
     * 
     * @param deviceId the number denoting the device (0 = 1st device, 1 = 2nd device, etc.)
     * 
     * @return the frame reader using the specified device
     * 
     * @throws FrameReaderException
     */
    public static OpenCVDeviceFrameReader open(int deviceId)
        throws FrameReaderException
    {
        OpenCVFrameGrabber fg = new OpenCVFrameGrabber(deviceId);
        return new OpenCVDeviceFrameReader(deviceId, fg);
    }
}
