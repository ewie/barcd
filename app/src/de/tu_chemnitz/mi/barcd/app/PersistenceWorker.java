package de.tu_chemnitz.mi.barcd.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

import de.tu_chemnitz.mi.barcd.Frame;
import de.tu_chemnitz.mi.barcd.Job;
import de.tu_chemnitz.mi.barcd.Serializer;
import de.tu_chemnitz.mi.barcd.SerializerException;

/**
 * A worker performing the persistence of frames and the job.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class PersistenceWorker extends Worker {
    private final Serializer<Job> jobSerializer;

    private final Serializer<Frame> frameSerializer;

    private final Job job;

    private final File jobFile;

    private final Mapper<Frame, File> frameFileMapper;

    private final LinkedList<Frame> frames = new LinkedList<Frame>();

    private final int maxQueueSize;

    /**
     * @param job the job to persist
     * @param jobFile the job file
     * @param frameFileMapper a mapper to get a frame's file
     * @param jobSerializer the job serializer
     * @param frameSerializer the frame serializer
     * @param maxQueueSize the maximum number of frames to be queued before
     *   being persisted in one go
     */
    public PersistenceWorker(
        Job job,
        File jobFile,
        Mapper<Frame, File> frameFileMapper,
        Serializer<Job> jobSerializer,
        Serializer<Frame> frameSerializer,
        int maxQueueSize)
    {
        this.job = job;
        this.jobFile = jobFile;
        this.frameFileMapper = frameFileMapper;
        this.jobSerializer = jobSerializer;
        this.frameSerializer = frameSerializer;
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * Queue a frame for persistence.
     *
     * @param frame the frame to be persisted
     *
     * @throws PersistenceWorkerException if the worker accepts no more frames
     *   because it's about to terminate
     */
    public void queueFrame(Frame frame)
        throws PersistenceWorkerException
    {
        if (shouldTerminate()) {
            throw new PersistenceWorkerException("can not acception more frames");
        }
        frames.add(frame);
    }

    @Override
    protected void work()
        throws PersistenceWorkerException
    {
        boolean persistJob = false;

        while (!shouldTerminate()) {
            while (frames.size() > maxQueueSize) {
                persistFrame(frames.remove());
                persistJob = true;
            }

            // Without sleeping this while loop runs infinitely even if
            // #shouldTerminate() returns true, i.e. the worker should terminate.
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                throw new PersistenceWorkerException("sleep got interrupted", ex);
            }
        }

        // Persist all remaining frames.
        while (!frames.isEmpty()) {
            persistFrame(frames.remove());
            persistJob = true;
        }

        // Persist the job only if at least one frame has been persisted.
        if (persistJob) {
            persistJob();
        }
    }

    private void persistFrame(Frame frame)
        throws PersistenceWorkerException
    {
        File frameFile;
        try {
            frameFile = frameFileMapper.map(frame);
        } catch (MapperException ex) {
            throw new PersistenceWorkerException("could not map frame to file", ex);
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(frameFile);
        } catch (FileNotFoundException ex) {
            throw new PersistenceWorkerException("chould not open frame file", ex);
        }

        try {
            frameSerializer.serialize(frame, out);
        } catch (SerializerException ex) {
            throw new PersistenceWorkerException("chould not serializer frame", ex);
        }
    }

    private void persistJob()
        throws PersistenceWorkerException
    {
        FileOutputStream out;
        try {
            out = new FileOutputStream(jobFile);
        } catch (FileNotFoundException ex) {
            throw new PersistenceWorkerException("chould not open job file", ex);
        }
        try {
            jobSerializer.serialize(job, out);
        } catch (SerializerException ex) {
            throw new PersistenceWorkerException("chould not serializer job", ex);
        }
    }
}
