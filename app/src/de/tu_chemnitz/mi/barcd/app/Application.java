package de.tu_chemnitz.mi.barcd.app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.ExtractorException;
import de.tu_chemnitz.mi.barcd.Frame;
import de.tu_chemnitz.mi.barcd.FrameHandler;
import de.tu_chemnitz.mi.barcd.Job;
import de.tu_chemnitz.mi.barcd.Region;
import de.tu_chemnitz.mi.barcd.app.Terminal.BoundCommand;
import de.tu_chemnitz.mi.barcd.app.Terminal.Command;
import de.tu_chemnitz.mi.barcd.app.Terminal.Routine;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.image.ScalingOperator;
import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequence;
import de.tu_chemnitz.mi.barcd.xml.XmlFrameSerializer;
import de.tu_chemnitz.mi.barcd.xml.XmlJobSerializer;
import de.tu_chemnitz.mi.barcd.xml.XmlSerializerException;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Application extends Worker {
    private static final String TERMINAL_PREFIX = "barcd>";

    private final Job job;

    private final Thread extractionThread;

    private final PersistenceWorker persistenceWorker;

    private final Thread persistenceThread;

    private final Thread terminalThread;

    private final Options options;

    private ImageDisplay display;

    private Terminal terminalWorker;

    private ExtractionWorker extractionWorker;

    public Application(Options options)
        throws ApplicationException
    {
        this.options = options;

        job = loadJob(options.getJobFile());

        extractionWorker = createExtractionWorker();
        persistenceWorker = createPersistenceWorker();

        extractionThread = new Thread(extractionWorker);
        persistenceThread = new Thread(persistenceWorker);

        terminalWorker = createTerminal(extractionThread, extractionWorker, persistenceThread, persistenceWorker);
        terminalThread = new Thread(terminalWorker);
    }

    @Override
    public void work()
        throws Exception
    {
        extractionThread.start();
        persistenceThread.start();
        terminalThread.start();
    }

    private void handleWorkerException(Exception ex) {
        terminalWorker.println(ex.getMessage());
        BoundCommand command = terminalWorker.getBoundCommand("stop");
        command.execute("");
    }

    private void stop() {
        extractionWorker.terminate();
        persistenceWorker.terminate();
        terminalWorker.terminate();
        if (display != null) {
            display.dispose();
        }
    }

    private ExtractionWorker createExtractionWorker()
        throws ApplicationException
    {
        Extractor extractor;

        try {
            extractor = new Extractor(job);
        } catch (ExtractorException ex) {
            throw new ApplicationException("could not create extractor", ex);
        }

        ExtractionWorker extractionWorker = new ExtractionWorker(extractor);

        extractionWorker.setFrameHandler(new FrameHandler() {
            @Override
            public void handleFrame(Frame frame, BufferedImage image) {
                try {
                    Application.this.handleFrame(frame, image);
                } catch (ApplicationException ex) {
                    throw new WorkerException(ex);
                }
            }
        });

        extractionWorker.setExceptionHandler(new WorkerExceptionHandler() {
            @Override
            public void handleException(Worker worker, Exception ex) {
                handleWorkerException(ex);
            }
        });

        return extractionWorker;
    }

    private PersistenceWorker createPersistenceWorker()
        throws ApplicationException
    {
        Mapper<Frame, File> frameFileMapper = new Mapper<Frame, File>() {
            @Override
            public File map(Frame frame)
                throws MapperException
            {
                TemplatedUrlSequence urls = job.getFrameUrlTemplate();
                URL url;
                try {
                    url = urls.getUrl(frame.getNumber());
                } catch (MalformedURLException ex) {
                    throw new MapperException("invalid frame URL", ex);
                }
                URI uri;
                try {
                    uri = url.toURI();
                } catch (URISyntaxException ex) {
                    throw new MapperException(ex);
                }
                return new File(uri);
            }
        };

        XmlJobSerializer jobSerializer = createJobSerializer();
        XmlFrameSerializer frameSerializer = createFrameSerializer();

        return new PersistenceWorker(
            job,
            options.getJobFile(),
            frameFileMapper,
            jobSerializer,
            frameSerializer,
            64);
    }

    private void handleFrame(Frame frame, BufferedImage image)
        throws ApplicationException
    {
        displayFrame(frame, image);
        if (options.getPersist()) {
            try {
                persistenceWorker.queueFrame(frame);
            } catch (PersistenceWorkerException ex) {
                throw new ApplicationException("could not persist the next frame", ex);
            }
        }
    }

    private void displayFrame(Frame frame, BufferedImage image) {
        if (!options.getDisplay()) return;
        if (display == null) display = new ImageDisplay();

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

    private XmlFrameSerializer createFrameSerializer()
        throws ApplicationException
    {
        XmlFrameSerializer serializer = new XmlFrameSerializer();

        try {
            serializer.setSchemaLocation(options.getXmlSchemaUrl());
        } catch (XmlSerializerException ex) {
            throw new ApplicationException("could not open XML schema", ex);
        }

        try {
            serializer.setValidate(true);
        } catch (XmlSerializerException ex) {
            throw new ApplicationException("XML schema not loaded", ex);
        }

        serializer.setIncludeSchemaLocation(true);
        serializer.setPretty(true);

        return serializer;
    }

    private XmlJobSerializer createJobSerializer()
        throws ApplicationException
    {
        XmlJobSerializer serializer = new XmlJobSerializer();

        try {
            serializer.setSchemaLocation(options.getXmlSchemaUrl());
        } catch (XmlSerializerException ex) {
            throw new ApplicationException("could not open XML schema", ex);
        }

        try {
            serializer.setValidate(true);
        } catch (XmlSerializerException ex) {
            throw new ApplicationException("XML schema not loaded", ex);
        }

        serializer.setIncludeSchemaLocation(true);
        serializer.setPretty(true);

        return serializer;
    }

    private Job loadJob(File file)
        throws ApplicationException
    {
        XmlJobSerializer sj = new XmlJobSerializer();

        URL contextUrl;

        try {
            contextUrl = file.getParentFile().toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new ApplicationException("malformed context URL", ex);
        }

        sj.setUrlContext(contextUrl);

        try {
            URL schemaUrl = options.getXmlSchemaUrl();
            if (schemaUrl != null) {
                sj.setSchemaLocation(options.getXmlSchemaUrl());
                sj.setValidate(true);
            }
        } catch (XmlSerializerException ex) {
            throw new ApplicationException("could not set XML schema location", ex);
        }

        FileInputStream fin;

        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new ApplicationException(String.format("job file (%s) not found", file), ex);
        }

        Job job;

        try {
            job = sj.unserialize(fin);
        } catch (XmlSerializerException ex) {
            throw new ApplicationException(String.format("job file (%s) could not be deserialized", file), ex);
        }

        return job;
    }

    private Terminal createTerminal(final Thread extractionThread,
                                    final ExtractionWorker extractionWorker,
                                    final Thread persistenceThread,
                                    final PersistenceWorker persistenceWorker)
        throws ApplicationException
    {
        Terminal terminal = new Terminal(System.in, System.out, TERMINAL_PREFIX);

        Routine stopRoutine = new Routine() {
            @Override
            public void execute(Terminal terminal, String args) {
                stop();
            }
        };

        Routine helpRoutine = new Routine() {
            @Override
            public void execute(Terminal terminal, String args) {
                Collection<Command> cc = terminal.getCommands();
                ArrayList<Command> cl = new ArrayList<Command>(cc);
                Collections.sort(cl, new Comparator<Command>() {
                    @Override
                    public int compare(Command x, Command y) {
                        return x.getName().compareTo(y.getName());
                    }
                });
                int maxNameLength = 0;
                for (Command c : cl) {
                    maxNameLength = Math.max(maxNameLength, c.getName().length());
                }
                for (Command c : cl) {
                    String s = Util.padRight(c.getName(), maxNameLength);
                    if (c.getDescription() != null) {
                        s += " :: ";
                        s += c.getDescription();
                    }
                    terminal.println(s);
                }
            }
        };

        Command stopCommand = new Command("stop", stopRoutine, "stop the extraction process");

        Command helpCommand = new Command("help", helpRoutine, "display this help");

        try {
            terminal.registerCommand(stopCommand);
            terminal.registerCommand(helpCommand);
        } catch (TerminalException ex) {
            throw new ApplicationException("could not setup terminal", ex);
        }

        return terminal;
    }
}
