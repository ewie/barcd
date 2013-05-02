package de.tu_chemnitz.mi.barcd.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line oriented terminal to read and handle commands. Commands must be
 * registered and are recognized by their unique (case-sensitive) name.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Terminal extends Worker {
    /**
     * The pattern to extract a command's name and arguments.
     */
    private static final Pattern COMMAND_LINE_PATTERN = Pattern.compile("^\\s*([^\\s]+)+(.*)$");

    /**
     * Map command names to the respective commands.
     */
    private final HashMap<String, Command> commands = new HashMap<String, Command>();

    private final BufferedReader reader;

    private final PrintStream printer;

    private final String prefix;

    private LinkedList<String> history = new LinkedList<String>();

    /**
     * Create a new terminal with input and print stream.
     *
     * @param in the source of the terminal's input
     * @param out the target of the terminal's output
     * @param prefix the command line prefix
     */
    public Terminal(InputStream in, PrintStream out, String prefix) {
        reader = new BufferedReader(new InputStreamReader(in));
        printer = out;
        this.prefix = prefix;
    }

    /**
     * Register a command with unique (case-sensitive) name.
     *
     * @param command the command to register
     *
     * @throws TerminalException
     *   if a command with the same case-sensitive name is already registered
     */
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

    /**
     * Get a collection of all registered commands.
     *
     * @return a read-only collection of all registered commands
     */
    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    /**
     * Get the command registered under the given name bounded to this terminal.
     *
     * @param name the command name
     *
     * @return the bound command or null if the command is no registered
     */
    public BoundCommand getBoundCommand(String name) {
        Command command = commands.get(name);
        if (command == null) {
            return null;
        }
        return new BoundCommand(command, this);
    }

    /**
     * Put a formatted string on the terminal's output.
     *
     * @param format
     * @param args
     *
     * @see PrintStream#printf(String, Object...)
     */
    public void printf(String format, Object... args) {
        printer.printf(format, args);
    }

    /**
     * Print an object on the terminal's output.
     *
     * @param x
     */
    public <T> void print(T x) {
        printer.print(x);
    }

    /**
     * Print an object on the terminal's output and terminate the line.
     *
     * @param x
     */
    public <T> void println(T x) {
        printer.println(x);
    }

    @Override
    protected void work()
        throws Exception
    {
        LineReader lineReader = new LineReader(reader);
        boolean newLine = true;
        while (!shouldTerminate()) {
            if (newLine) {
                print(prefix + " ");
            }
            newLine = false;
            String line = lineReader.readLine();
            if (line == null) {
                continue;
            }
            if (line.isEmpty()) {
                executeLastCommand();
            } else {
                executeCommand(line, true);
            }
            newLine = true;
        }
    }

    private void executeLastCommand() {
        if (history.size() == 0) {
            println("!!! nothing to repeat");
        } else {
            String line = history.getFirst();
            println("[repeat] " + line);
            executeCommand(line, false);
        }
    }

    /**
     * @param line the command line to execute
     * @param record whether to record the command (only possible if the command
     *   is recordable)
     */
    private void executeCommand(String line, boolean record) {
        Matcher matcher = COMMAND_LINE_PATTERN.matcher(line);
        if (matcher.matches()) {
            String name = matcher.group(1);
            String args = matcher.group(2).trim();
            Command command = commands.get(name);
            if (command == null) {
                println("!!! unknown command \"" + name + "\"");
            } else {
                command.execute(this, args);
                if (record) {
                    history.addFirst(line);
                }
            }
        } else {
            println("!!! malformed command \"" + line + "\"");
        }
    }

    /**
     * A command bound to a specific terminal.
     *
     * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
     */
    public static class BoundCommand {
        private Command command;
        private Terminal terminal;

        /**
         * @param command the command
         * @param terminal the terminal the command should be bound to
         */
        public BoundCommand(Command command, Terminal terminal) {
            this.command = command;
            this.terminal = terminal;
        }

        /**
         * @param args
         */
        public void execute(String args) {
            command.execute(terminal, args);
        }
    }

    /**
     * A command handles an action read from the a terminal's input by passing
     * it to its routine.
     *
     * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
     */
    public static abstract class Command {
        private String name;
        private String description;

        /**
         * Create a command with description.
         *
         * @param name the command's name
         * @param description the command's description
         */
        public Command(String name, String description) {
            this.name = name;
            this.description = description;
        }

        /**
         * Create a recordable command with its description set to null.
         *
         * @param name the command's name
         * @param routine the command's routine
         */
        public Command(String name) {
            this(name, null);
        }

        /**
         * Get the command name.
         *
         * @return the command's name
         */
        public final String getName() {
            return name;
        }

        /**
         * Get the command description.
         *
         * @return the command's description or null if not set
         */
        public final String getDescription() {
            return description;
        }

        /**
         * @param terminal the terminal this routine's command is registered in
         * @param args the raw argument string passed along with the command
         */
        public abstract void execute(Terminal terminal, String args);
    }

    /**
     * A non-blocking line reader.
     *
     * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
     */
    private static class LineReader {
        private final BufferedReader reader;
        private StringBuilder lineBuffer;

        /**
         * @param reader the underlying reader
         */
        public LineReader(BufferedReader reader) {
            this.reader = reader;
        }

        /**
         * Read the next character if the underlying reader is ready. Returns a
         * string if a full line (indicated by a line feed U+000A or carriage
         * return U+000D).
         *
         * @return a complete line without line feed or carriage return, null if
         *   no complete line has been read so far
         */
        public String readLine() {
            if (lineBuffer == null) {
                lineBuffer = new StringBuilder();
            }
            boolean ready = false;
            try {
                ready = reader.ready();
            } catch (IOException ex) {
                return null;
            }
            if (ready) {
                int c;
                try {
                    c = reader.read();
                } catch (IOException ex) {
                    return null;
                }
                boolean complete = c == '\n' || c == '\r';
                if (!complete) {
                    lineBuffer.append((char) c);
                }
                if (complete) {
                    String s = lineBuffer.toString();
                    lineBuffer = null;
                    return s;
                }
            }
            return null;
        }
    }
}
