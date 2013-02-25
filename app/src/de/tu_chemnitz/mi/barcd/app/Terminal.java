package de.tu_chemnitz.mi.barcd.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Terminal {
    public static interface Routine {
        public void execute(Terminal terminal);
    }
    
    public static class Command {
        private String name;
        private String description;
        private Routine routine;

        public Command(String name, Routine routine, String description) {
            this.name = name;
            this.description = description;
            this.routine = routine;
        }

        public Command(String name, Routine routine) {
            this(name, routine, null);
        }
        
        public final String getName() {
            return name;
        }
        
        public final String getDescription() {
            return description;
        }
        
        public void execute(Terminal terminal) {
            routine.execute(terminal);
        }
    }
    
    private HashMap<String, Command> commands;
    
    private BufferedReader reader;

    private PrintStream out;

    private String prefix;
    
    public Terminal(InputStream in, final PrintStream out) {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.out = out;
        commands = new HashMap<String, Command>();
    }
    
    public void registerCommand(Command command)
        throws TerminalException
    {
        String name = command.getName();
        if (commands.containsKey(name)) {
            throw new TerminalException(
                String.format("command \"%s\" already registered", name));
        }
        commands.put(name, command);
    }
    
    public Collection<Command> commands() {
        return commands.values();
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void printf(String format, Object... args) {
        out.printf(format, args);
    }
    
    public void processNextLine()
        throws IOException
    {
        printf(prefix + " ");
        String line = reader.readLine();
        if (line != null) {
            Command command = commands.get(line);
            if (command == null) {
                printf("!!! unrecognized command \"%s\"\n", line);
            } else {
                command.execute(this);
            }
        }
    }
}
