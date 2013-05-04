package de.tu_chemnitz.mi.barcd.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

import de.tu_chemnitz.mi.barcd.Extraction;
import de.tu_chemnitz.mi.barcd.Job;
import de.tu_chemnitz.mi.barcd.Serializer;
import de.tu_chemnitz.mi.barcd.SerializerException;

/**
 * A worker performing the persistence of a job and its extractions
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class PersistenceWorker extends Worker {
    private final Serializer<Job> jobSerializer;

    private final Serializer<Extraction> extractionSerializer;

    private final Job job;

    private final File jobFile;

    private final Mapper<Extraction, File> extractionFileMapper;

    private final LinkedList<Extraction> extractions = new LinkedList<Extraction>();

    private final int maxQueueSize;

    /**
     * @param job the job to persist
     * @param jobFile the job file
     * @param extractionFileMapper a mapper to get an extraction's file
     * @param jobSerializer the job serializer
     * @param extractionSerializer the extraction serializer
     * @param maxQueueSize the maximum number of frames to be queued before
     *   being persisted in one go
     */
    public PersistenceWorker(
        Job job,
        File jobFile,
        Mapper<Extraction, File> extractionFileMapper,
        Serializer<Job> jobSerializer,
        Serializer<Extraction> extractionSerializer,
        int maxQueueSize)
    {
        this.job = job;
        this.jobFile = jobFile;
        this.extractionFileMapper = extractionFileMapper;
        this.jobSerializer = jobSerializer;
        this.extractionSerializer = extractionSerializer;
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * Queue an extraction for persistence.
     *
     * @param extraction the extraction to be persisted
     *
     * @throws PersistenceWorkerException if the worker accepts no more
     *   extractions because it's about to terminate
     */
    public void queueExtraction(Extraction extraction)
        throws PersistenceWorkerException
    {
        if (shouldTerminate()) {
            throw new PersistenceWorkerException("can not accept more extractions");
        }
        extractions.add(extraction);
    }

    @Override
    protected void work()
        throws PersistenceWorkerException
    {
        boolean persistJob = false;

        while (!shouldTerminate()) {
            while (extractions.size() > maxQueueSize) {
                persistExtraction(extractions.remove());
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

        // Persist all remaining extractions.
        while (!extractions.isEmpty()) {
            persistExtraction(extractions.remove());
            persistJob = true;
        }

        // Persist the job only if at least one extraction has been persisted.
        if (persistJob) {
            persistJob();
        }
    }

    private void persistExtraction(Extraction extraction)
        throws PersistenceWorkerException
    {
        File extractionFile;
        try {
            extractionFile = extractionFileMapper.map(extraction);
        } catch (MapperException ex) {
            throw new PersistenceWorkerException("could not map extraction to file", ex);
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(extractionFile);
        } catch (FileNotFoundException ex) {
            throw new PersistenceWorkerException("chould not open extraction file", ex);
        }

        try {
            extractionSerializer.serialize(extraction, out);
        } catch (SerializerException ex) {
            throw new PersistenceWorkerException("chould not serializer extraction", ex);
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
