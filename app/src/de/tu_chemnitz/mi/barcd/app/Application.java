package de.tu_chemnitz.mi.barcd.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import de.tu_chemnitz.mi.barcd.Extractor;
import de.tu_chemnitz.mi.barcd.ImageProviderException;
import de.tu_chemnitz.mi.barcd.Job;
import de.tu_chemnitz.mi.barcd.app.Terminal.Command;
import de.tu_chemnitz.mi.barcd.app.Terminal.Handler;
import de.tu_chemnitz.mi.barcd.xml.XMLJobSerializer;
import de.tu_chemnitz.mi.barcd.xml.XMLSerializerException;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Application implements Runnable {
    private Job job;
    
    private Extractor extractor;

    private Thread workerThread;

    private Terminal terminal;
    
    public Application(String[] args)
        throws ApplicationException
    {
        CommandLineOptions options = parseArguments(args);
        job = loadJob(options.getJobFilePath());
        try {
            extractor = new Extractor(job);
        } catch (ImageProviderException ex) {
            throw new ApplicationException("could not create image provider", ex);
        }
        Worker worker = new Worker(extractor);
        workerThread = new Thread(worker);
        terminal = setupTerminal(workerThread, worker);
    }
    
    @Override
    public void run() {
        workerThread.start();
        while (workerThread.isAlive()) {
            try {
                terminal.processNextLine();
            } catch (IOException ex) {
                // TODO maybe just ignore?
                throw new RuntimeException(ex);
            }
        }
        // TODO persist extractions
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
        
        URL schemaURL = null;
        try {
            schemaURL = new URL("file:/home/ewie/workspace/barcd/core/etc/barcd.xsd");
        } catch (MalformedURLException ex) {
            throw new ApplicationException("could not open XML schema", ex);
        }
        
        XMLJobSerializer sj = new XMLJobSerializer();
        try {
            try {
                URL contextURL = file.getParentFile().toURI().toURL();
                sj.setURLContext(contextURL);
            } catch (MalformedURLException ex) {
                throw new ApplicationException("could not set URL context", ex);
            }
            sj.setSchemaLocation(schemaURL);
            sj.setValidation(true);
            job = sj.unserialize(fin);
        } catch (XMLSerializerException ex) {
            throw new ApplicationException(String.format("job file (%s) not found", file), ex);
        }
        
        return job;
    }
    
    private Terminal setupTerminal(final Thread thread, final Worker worker)
        throws ApplicationException
    {
        Terminal terminal = new Terminal(System.in, System.out);
        
        terminal.setPrefix(">>>");
        
        Handler stopHandler = new Handler() {
            @Override
            public void execute(Terminal terminal) {
                worker.teminate();
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        
        Handler quitHandler = new Handler() {
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
        
        Command stopCommand = new Command("stop", stopHandler,
            "stop the extraction process and persist all extractions so far");
        
        Command quitCommand = new Command("help", quitHandler, "display this help");
        
        try {
            terminal.registerCommand(stopCommand);
            terminal.registerCommand(quitCommand);
        } catch (TerminalException ex) {
            throw new ApplicationException("could not setup terminal", ex);
        }
        
        return terminal;
    }
}
