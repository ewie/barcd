/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.source;

import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.provider.VideoImageProvider;
import de.tu_chemnitz.mi.barcd.video.FrameReader;
import de.tu_chemnitz.mi.barcd.video.FrameReaderException;
import de.tu_chemnitz.mi.barcd.video.OpenCvDeviceFrameReader;

/**
 * An image source using a camera device addressed by its number (0 for the
 * first device, 1 for the second device, etc).
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class VideoDeviceSource extends Source {
    private final int deviceNumber;

    public VideoDeviceSource(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    @Override
    public VideoImageProvider createImageProvider(int initialFrameNumber)
        throws ImageProviderException
    {
        try {
            FrameReader fr = OpenCvDeviceFrameReader.open(deviceNumber);
            return new VideoImageProvider(fr);
        } catch (FrameReaderException ex) {
            throw new ImageProviderException("cannot create image provider", ex);
        }
    }
}
