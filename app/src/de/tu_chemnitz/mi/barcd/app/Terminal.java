package de.tu_chemnitz.mi.barcd.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A line oriented terminal to read and handle commands. Commands must be
 * registered and are recognized by their unique (case-sensitive) name.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Terminal {
    /**
     * The pattern to extract a command's name and arguments.
     */
    private Pattern COMMAND_LINE_PATTERN = Pattern.compile("^\\s*([^\\s]+)+(.*)$");

    /**
     * Map command names to the respective commands.
     */
    private HashMap<String, Command> commands = new HashMap<String, Command>();

    private BufferedReader reader;

    private PrintStream printer;

    private String prefix;

    /**
     * Create a new terminal with input and print stream.
     *
     * @param in the source of the terminal's input
     * @param out the target of the terminal's output
     */
    public Terminal(InputStream in, PrintStream out) {
        reader = new BufferedReader(new InputStreamReader(in));
        printer = out;
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
    public Collection<Command> commands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    /**
     * Set the prefix indicating the terminal's input.
     *
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Get the terminal's input prefix.
     *
     * @return the input prefix
     */
    public String getPrefix() {
        return prefix;
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

    /**
     * Indicate the terminal to process the next input line.
     *
     * @throws IOException
     */
    public void processNextLine()
        throws IOException
    {
        printf(prefix + " ");
        String line = reader.readLine();
        if (line != null) {
            Matcher matcher = COMMAND_LINE_PATTERN.matcher(line);
            if (matcher.matches()) {
                String name = matcher.group(1);
                String args = matcher.group(2).trim();
                Command command = commands.get(name);
                if (command == null) {
                    println("!!! unrecognized command \"" + name + "\"");
                } else {
                    command.execute(this, args);
                }
            } else {
                println("!!! malformed command \"" + line + "\"");
            }
        }
    }

    /**
     * A command handles an action read from the a terminal's input by passing
     * it to its routine.
     *
     * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
     */
    public static class Command {
        private String name;
        private String description;
        private Routine routine;

        /**
         * Create a command with description.
         *
         * @param name the command's name
         * @param routine the command's routine
         * @param description the command's description
         */
        public Command(String name, Routine routine, String description) {
            this.name = name;
            this.description = description;
            this.routine = routine;
        }

        /**
         * Create a command with its description set to null.
         *
         * @param name the command's name
         * @param routine the command's routine
         */
        public Command(String name, Routine routine) {
            this(name, routine, null);
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
        public void execute(Terminal terminal, String args) {
            routine.execute(terminal, args);
        }
    }

    /**
     * The routine implementing a command's action.
     *
     * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
     */
    public static interface Routine {
        /**
         * @param terminal the terminal this routine's command is registered in
         * @param args the raw argument string passed along with the command
         */
        public void execute(Terminal terminal, String args);
    }
}
