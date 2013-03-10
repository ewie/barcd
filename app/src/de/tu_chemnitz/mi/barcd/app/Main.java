package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Main {
    public static void main(String[] args) {
        CommandLineOptionsParser parser = new CommandLineOptionsParser();
        Options options = parser.parse(args);

        try {
            Application app = new Application(options);
            app.run();
        } catch (WorkerException ex) {
            System.err.println(ex.getMessage());
        } catch (ApplicationException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
