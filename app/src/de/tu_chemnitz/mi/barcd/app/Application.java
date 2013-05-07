/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
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

import de.tu_chemnitz.mi.barcd.Extraction;
import de.tu_chemnitz.mi.barcd.ExtractionHandler;
import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.ExtractorException;
import de.tu_chemnitz.mi.barcd.Job;
import de.tu_chemnitz.mi.barcd.Region;
import de.tu_chemnitz.mi.barcd.app.Terminal.BoundCommand;
import de.tu_chemnitz.mi.barcd.app.Terminal.Command;
import de.tu_chemnitz.mi.barcd.geometry.Point;
import de.tu_chemnitz.mi.barcd.image.ScalingOperator;
import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequence;
import de.tu_chemnitz.mi.barcd.xml.XmlExtractionSerializer;
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

    private final ImageDisplay display;

    private final Terminal terminalWorker;

    private final ExtractionWorker extractionWorker;

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

        display = new ImageDisplay();
        display.setVisible(options.getDisplay());
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
        terminalWorker.printf("error encountered, stopping...\n\n");
        BoundCommand command = terminalWorker.getBoundCommand("stop");
        command.execute("");
        handleException(ex);
    }

    private void stop() {
        terminalWorker.terminate();
        extractionWorker.terminate();
        persistenceWorker.terminate();
        display.dispose();
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

        extractionWorker.setExtractionHandler(new ExtractionHandler() {
            @Override
            public void handleExtraction(Extraction extraction, BufferedImage image) {
                try {
                    Application.this.handleExtraction(extraction, image);
                } catch (ApplicationException ex) {
                    throw new WorkerException(ex);
                }
            }
        });

        extractionWorker.setExceptionHandler(new WorkerExceptionHandler() {
            @Override
            public void handleException(Worker worker, Exception ex) {
                Application.this.handleWorkerException(ex);
            }
        });

        return extractionWorker;
    }

    private PersistenceWorker createPersistenceWorker()
        throws ApplicationException
    {
        Mapper<Extraction, File> extractionFileMapper = new Mapper<Extraction, File>() {
            @Override
            public File map(Extraction extraction)
                throws MapperException
            {
                TemplatedUrlSequence urls = job.getExtractionUrlTemplate();
                URL url;
                try {
                    url = urls.getUrl(extraction.getFrameNumber());
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
        XmlExtractionSerializer extractionSerializer = createExtractionSerializer();

        return new PersistenceWorker(
            job,
            options.getJobFile(),
            extractionFileMapper,
            jobSerializer,
            extractionSerializer,
            64);
    }

    private void handleExtraction(Extraction extraction, BufferedImage image)
        throws ApplicationException
    {
        displayFrame(extraction, image);
        if (options.getPersist()) {
            try {
                persistenceWorker.queueExtraction(extraction);
            } catch (PersistenceWorkerException ex) {
                throw new ApplicationException("could not persist the next frame", ex);
            }
        }
    }

    private void displayFrame(Extraction extraction, BufferedImage image) {
        if (!display.isVisible()) {
            return;
        }

        ScalingOperator scaling = new ScalingOperator();
        BufferedImage im = scaling.apply(image, 1000);
        double scale = (double) im.getWidth() / image.getWidth();

        Graphics2D g = im.createGraphics();

        for (Region r : extraction.getRegions()) {
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

    private XmlExtractionSerializer createExtractionSerializer()
        throws ApplicationException
    {
        XmlExtractionSerializer serializer = new XmlExtractionSerializer();

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

        Command stopCommand = new Command("stop", "stop the extraction process") {
            @Override
            public void execute(Terminal terminal, String args) {
                stop();
            }
        };

        Command helpCommand = new Command("help", "display this help") {
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

        Command displayCommad = new Command("display", "toggle display [display on|off]") {
            private static final String OPTION_ON = "on";

            private static final String OPTION_OFF = "off";

            @Override
            public void execute(Terminal terminal, String args) {
                if (args.equals(OPTION_ON)) {
                    display.setVisible(true);
                } else if (args.equals(OPTION_OFF)) {
                    display.setVisible(false);
                } else {
                    terminal.println("!!! unrecognized display option \"" + args + "\"");
                }
            }
        };

        try {
            terminal.registerCommand(stopCommand);
            terminal.registerCommand(helpCommand);
            terminal.registerCommand(displayCommad);
        } catch (TerminalException ex) {
            throw new ApplicationException("could not setup terminal", ex);
        }

        return terminal;
    }
}
