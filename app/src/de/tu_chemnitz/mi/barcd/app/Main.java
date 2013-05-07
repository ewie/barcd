/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.app;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class Main {
    private static final String COPYRIGHT_NOTICE =
        "Copyright (c) 2012-2013 Erik Wienhold & René Richter" + '\n' +
        "Licensed under the BSD 3-Clause License." + '\n';

    public static void main(String[] args) {
        printCopyrightNotice();
        Options options = parseCommandLineArguments(args);
        createAndRunApplication(options);
    }

    private static void createAndRunApplication(Options options) {
        try {
            Application app = new Application(options);
            app.run();
        } catch (WorkerException ex) {
            System.err.println(ex.getMessage());
        } catch (ApplicationException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static Options parseCommandLineArguments(String[] args) {
        CommandLineArgumentsParser parser = new CommandLineArgumentsParser();
        return parser.parse(args);
    }

    private static void printCopyrightNotice() {
        System.out.println(COPYRIGHT_NOTICE);
    }
}
