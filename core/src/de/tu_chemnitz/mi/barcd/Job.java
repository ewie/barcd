package de.tu_chemnitz.mi.barcd;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Job {
    private Source source;
    
    private Collection<Frame> frames;
    
    private int initialFrameNumber;

    private ImageProvider imageProvider;
    
    public Job(Source source, int initialFrameNumber) {
        if (initialFrameNumber < Source.INITIAL_FRAME_NUMBER) {
            throw new IllegalArgumentException("+initialFrameNumber+ must be greater " + Source.INITIAL_FRAME_NUMBER);
        }
        this.source = source;
        this.initialFrameNumber = initialFrameNumber;
        this.frames = new LinkedList<Frame>();
    }
    
    public Job(Source source) {
        this(source, Source.INITIAL_FRAME_NUMBER);
    }
    
    public Source getSource() {
        return source;
    }
    
    public int nextFrameNumber() {
        return initialFrameNumber + frames.size();
    }
    
    public ImageProvider createImageProvider()
        throws ImageProviderException
    {
        return source.createImageProvider(initialFrameNumber);
    }
    
    public ImageProvider getImageProvider()
        throws ImageProviderException
    {
        if (imageProvider == null) {
            imageProvider = createImageProvider();
        }
        return imageProvider;
    }
    
    public Collection<Frame> getFrames() {
        return frames;
    }

    public Frame createFrame() {
        Frame frame = new Frame(nextFrameNumber());
        frames.add(frame);
        return frame;
    }
}
