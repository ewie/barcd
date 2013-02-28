package de.tu_chemnitz.mi.barcd;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Job {
    public static final int INITIAL_FRAME_NUMBER = 0;
    
    private Source source;
    
    private Collection<Frame> frames;
    
    public Job(Source source) {
        this.source = source;
        this.frames = new LinkedList<Frame>();
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
        Frame frame = new Frame(frames.size());
        frames.add(frame);
        return frame;
    }
}
