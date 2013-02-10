package de.tu_chemnitz.mi.barcd;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Job {
    private Source source;
    
    private Collection<Frame> frames;
    
    private int nextFrameNumber;
    
    public Job(Source source, int nextFrameNumber) {
        this.source = source;
        this.frames = new LinkedList<Frame>();
        this.nextFrameNumber = nextFrameNumber;
    }
    
    public Job(Source source) {
        this(source, 0);
    }
    
    public Source getSource() {
        return source;
    }
    
    public ImageProvider getImageProvider()
        throws ImageProviderException
    {
        return source.getImageProvider();
    }
    
    public Collection<Frame> getFrames() {
        return frames;
    }

    public Frame createFrame() {
        Frame frame = new Frame(nextFrameNumber++);
        frames.add(frame);
        return frame;
    }
    
    public int getNextFrameNumber() {
        return nextFrameNumber;
    }
}
