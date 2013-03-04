package de.tu_chemnitz.mi.barcd.video;

/**
 * A seekable frame reader allowing for random access of frames using the
 * corresponding frame numbers.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface SeekableFrameReader extends FrameReader {
    /**
     * @return the number of frames the reader can serve
     */
    public int getLengthInFrames();

    /**
     * Skip the next frames.
     *
     * @param count the number of frames to skip
     *
     * @throws FrameReaderException if the new frame number is illegal
     */
    public void skipFrames(int count) throws FrameReaderException;

    /**
     * Set the frame number of the next frame to be read.
     *
     * @param frameNumber
     *
     * @throws FrameReaderException if the frame number is illegal
     */
    public void setFrameNumber(int frameNumber) throws FrameReaderException;
}
