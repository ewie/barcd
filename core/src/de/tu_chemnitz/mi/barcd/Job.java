package de.tu_chemnitz.mi.barcd;

import java.util.Collection;
import java.util.LinkedList;

import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequence;

/**
 * Provides the information to perform an extraction job and resume a job after
 * it has been stopped.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Job {
    private final Source source;

    private final Collection<Extraction> extractions;

    private final int initialFrameNumber;

    private int nextFrameNumber;

    private TemplatedUrlSequence extractionUrlTemplate;

    /**
     * Create a job with a specific initial frame number. Use this constructor
     * to create job which should be resumed after being stopped.
     *
     * @param source the image source
     * @param extractionUrlTemplate the URL template to generate a URL for each
     *   extraction to be persisted
     * @param initialFrameNumber the number of the first frame an image provider
     *   of the given source should provide
     */
    public Job(Source source, TemplatedUrlSequence extractionUrlTemplate, int initialFrameNumber) {
        if (initialFrameNumber < Source.INITIAL_FRAME_NUMBER) {
            throw new IllegalArgumentException("+initialFrameNumber+ must be greater " + Source.INITIAL_FRAME_NUMBER);
        }
        this.source = source;
        this.initialFrameNumber = initialFrameNumber;
        nextFrameNumber = initialFrameNumber;
        extractions = new LinkedList<Extraction>();
        this.extractionUrlTemplate = extractionUrlTemplate;
    }

    /**
     * Create a job with initial frame number zero.
     *
     * @param source the image source
     * @param frameUrlTemplate the URL template to generate a URL for each
     *   extraction to be persisted
     */
    public Job(Source source, TemplatedUrlSequence frameUrlTemplate) {
        this(source, frameUrlTemplate, Source.INITIAL_FRAME_NUMBER);
    }

    /**
     * @return the image source
     */
    public Source getSource() {
        return source;
    }

    /**
     * @return the extraction URL template
     */
    public TemplatedUrlSequence getExtractionUrlTemplate() {
        return extractionUrlTemplate;
    }

    /**
     * Get the next frame number starting from the initial frame number and
     * advancing by 1 with each created frame.
     *
     * @return the next frame number
     */
    public int getNextFrameNumber() {
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
     * Get all extractions produced so far.
     *
     * @return a collection of processed frames
     */
    public Collection<Extraction> getExtractions() {
        return extractions;
    }

    /**
     * Create a new extraction assigning the next frame number. Advance the next
     * frame number by 1.
     *
     * @return the created frame
     */
    public Extraction createExtraction() {
        Extraction frame = new Extraction(nextFrameNumber++);
        extractions.add(frame);
        return frame;
    }
}
