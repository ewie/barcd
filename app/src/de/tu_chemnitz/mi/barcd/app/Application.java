package de.tu_chemnitz.mi.barcd.app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.Extractor.FrameHandler;
import de.tu_chemnitz.mi.barcd.Frame;
import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Job;
import de.tu_chemnitz.mi.barcd.Region;
import de.tu_chemnitz.mi.barcd.app.Terminal.Command;
import de.tu_chemnitz.mi.barcd.app.Terminal.Routine;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.image.ScalingOperator;
import de.tu_chemnitz.mi.barcd.xml.XMLFrameSerializer;
import de.tu_chemnitz.mi.barcd.xml.XMLJobSerializer;
import de.tu_chemnitz.mi.barcd.xml.XMLSerializerException;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Application extends Worker {
    private static final String TERMINAL_PREFIX = "barcd>";

    private Job job;

    private Extractor extractor;

    private Thread extractionThread;

    private Terminal terminal;

    private CommandLineOptions options;

    private final VideoImageDisplay display = new VideoImageDisplay();

    public Application(String[] args)
        throws ApplicationException
    {
        options = parseArguments(args);
        job = loadJob(options.getJobFilePath());
        try {
            extractor = new Extractor(job);
        } catch (ImageProviderException ex) {
            throw new ApplicationException("could not create image provider", ex);
        }
        ExtractionWorker extractionWorker = new ExtractionWorker(extractor);
        extractionWorker.setFrameHandler(new FrameHandler() {
            @Override
            public void handleFrame(Frame frame, BufferedImage image) {
                displayFrame(frame, image);
                /*
                try {
                    persistFrame(frame);
                } catch (ApplicationException ex) {
                    throw new WorkerException(ex);
                }
                */
            }
        });
        extractionThread = new Thread(extractionWorker);
        terminal = setupTerminal(extractionThread, extractionWorker);
    }

    @Override
    public void work()
        throws Exception
    {
        extractionThread.start();
        while (extractionThread.isAlive()) {
            try {
                terminal.processNextLine();
            } catch (IOException ex) {
                // TODO maybe just ignore?
                throw new RuntimeException(ex);
            }
        }
    }

    private void displayFrame(Frame frame, BufferedImage image) {
        ScalingOperator scaling = new ScalingOperator();
        BufferedImage im = scaling.apply(image, 1000);
        double scale = (double) im.getWidth() / image.getWidth();

        Graphics2D g = im.createGraphics();

        for (Region r : frame.getRegions()) {
            Polygon polygon = new Polygon();
            for (Point p : r.getOrientedRectangle().getVertices()) {
                polygon.addPoint((int) (p.getX() * scale), (int) (p.getY() * scale));
            }
            float cov = (float) r.getCoverage();
            g.setColor(new Color(0f, 0f, 1f, cov));
            g.fillPolygon(polygon);

            polygon = new Polygon();
            for (Point p : r.getConvexPolygon().getVertices()) {
                polygon.addPoint((int) (p.getX() * scale), (int) (p.getY() * scale));
            }
            g.setColor(new Color(1f, 0f, 0f, cov));
            g.fillPolygon(polygon);
        }

        g.dispose();
        display.setImage(im);
    }

    private void persistFrame(Frame frame)
        throws ApplicationException
    {
        URI frameUri;
        try {
            URL frameUrl = options.getFrames().getURL(frame.getNumber());
            frameUri = frameUrl.toURI();
        } catch (MalformedURLException ex) {
            throw new ApplicationException("invalid file URL for frame " + frame.getNumber(), ex);
        } catch (URISyntaxException ex) {
            throw new ApplicationException("invalid file URL for frame " + frame.getNumber(), ex);
        }

        File frameFile = new File(frameUri);
        FileOutputStream frameOut;
        try {
            frameOut = new FileOutputStream(frameFile);
        } catch (FileNotFoundException ex) {
            throw new ApplicationException("could not open or create frame file: " + frameFile.toString(), ex);
        }

        XMLFrameSerializer fs = new XMLFrameSerializer();
        fs.setPretty(true);
        try {
            fs.serialize(frame, frameOut);
        } catch (XMLSerializerException ex) {
            throw new ApplicationException("could not serialize or persist frame", ex);
        }

        persistJob();
    }

    private void persistJob()
        throws ApplicationException
    {
        File jobFile = new File(options.getJobFilePath());
        FileOutputStream jobOut;

        try {
            jobOut = new FileOutputStream(jobFile);
        } catch (FileNotFoundException ex) {
            throw new ApplicationException("could not open or create job file: " + jobFile.toString(), ex);
        }

        XMLJobSerializer js = new XMLJobSerializer();
        js.setPretty(true);
        try {
            js.serialize(job, jobOut);
        } catch (XMLSerializerException ex) {
            throw new ApplicationException("could not serialize or persist job", ex);
        }
    }

    private CommandLineOptions parseArguments(String[] args)
        throws ApplicationException
    {
        CommandLineOptions options = new CommandLineOptions();

        if (!options.parse(args)) {
            throw new ApplicationException("could not parse arguments");
        }

        return options;
    }

    private Job loadJob(String path)
        throws ApplicationException
    {
        Job job = null;

        File file = new File(path);
        if (!file.exists()) {
            throw new ApplicationException(String.format("job file (%s) does not exist", file));
        }

        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new ApplicationException(String.format("job file (%s) not found", file), ex);
        }

        XMLJobSerializer sj = new XMLJobSerializer();

        URL contextURL;

        try {
            contextURL = file.getParentFile().toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new ApplicationException("malformed context URL", ex);
        }

        sj.setURLContext(contextURL);

        try {
            URL schemaUrl = options.getXmlSchemaUrl();
            if (schemaUrl != null) {
                sj.setSchemaLocation(options.getXmlSchemaUrl());
                sj.setValidation(true);
            }
        } catch (XMLSerializerException ex) {
            throw new ApplicationException("could not set XML schema location", ex);
        }

        try {
            job = sj.unserialize(fin);
        } catch (XMLSerializerException ex) {
            throw new ApplicationException(String.format("job file (%s) could not be deserialized", file), ex);
        }

        return job;
    }

    private Terminal setupTerminal(final Thread thread, final ExtractionWorker worker)
        throws ApplicationException
    {
        Terminal terminal = new Terminal(System.in, System.out);

        terminal.setPrefix(TERMINAL_PREFIX);

        Routine stopRoutine = new Routine() {
            @Override
            public void execute(Terminal terminal) {
                worker.terminate();
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

        Routine quitRoutine = new Routine() {
            @Override
            public void execute(Terminal terminal) {
                Collection<Command> cc = terminal.commands();
                ArrayList<Command> cl = new ArrayList<Command>(cc);
                Collections.sort(cl, new Comparator<Command>() {
                    @Override
                    public int compare(Command x, Command y) {
                        return x.getName().compareTo(y.getName());
                    }
                });
                for (Command c : cl) {
                    terminal.printf("%s : %s\n", c.getName(), c.getDescription());
                }
            }
        };

        Command stopCommand = new Command("stop", stopRoutine,
            "stop the extraction process and persist all extractions so far");

        Command quitCommand = new Command("help", quitRoutine, "display this help");

        try {
            terminal.registerCommand(stopCommand);
            terminal.registerCommand(quitCommand);
        } catch (TerminalException ex) {
            throw new ApplicationException("could not setup terminal", ex);
        }

        return terminal;
    }
}
