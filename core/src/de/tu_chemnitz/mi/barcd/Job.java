package de.tu_chemnitz.mi.barcd;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Provides the information to perform an extraction job and resume a job after
 * it has been stopped.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Job {
    private final Source source;

    private final Collection<Frame> frames;

    private final int initialFrameNumber;

    private int nextFrameNumber;

    /**
     * Create a job with a specific initial frame number. Use this constructor
     * to create job which should be resumed after being stopped.
     *
     * @param source the image source
     * @param initialFrameNumber the number of the first frame an image provider
     *   of the given source should provide
     */
    public Job(Source source, int initialFrameNumber) {
        if (initialFrameNumber < Source.INITIAL_FRAME_NUMBER) {
            throw new IllegalArgumentException("+initialFrameNumber+ must be greater " + Source.INITIAL_FRAME_NUMBER);
        }
        this.source = source;
        this.initialFrameNumber = initialFrameNumber;
        nextFrameNumber = initialFrameNumber;
        frames = new LinkedList<Frame>();
    }

    /**
     * Create a job with initial frame number zero.
     *
     * @param source the image source
     */
    public Job(Source source) {
        this(source, Source.INITIAL_FRAME_NUMBER);
    }

    /**
     * @return the image source
     */
    public Source getSource() {
        return source;
    }

    /**
     * Get the next frame number starting from the initial frame number and
     * advancing by 1 with each processed frame.
     *
     * @return the next frame number
     */
    public int nextFrameNumber() {
        return nextFrameNumber;
    }

    /**
     * Create an image provider using the job's {@link #getSource() source}.
     * The image provider starts at the initial frame number.
     *
     * @return a new image provider
     *
     * @throws ImageProviderException if the image provider could not be created
     */
    public ImageProvider createImageProvider()
        throws ImageProviderException
    {
        return source.createImageProvider(initialFrameNumber);
    }

    /**
     * Get all frames processed so far.
     *
     * @return a collection of processed frames
     */
    public Collection<Frame> getFrames() {
        return frames;
    }

    /**
     * Create a new frame assigning the next frame number. Advances the next
     * frame number by 1.
     *
     * @return the created frame
     */
    public Frame createFrame() {
        Frame frame = new Frame(nextFrameNumber++);
        frames.add(frame);
        return frame;
    }
}
