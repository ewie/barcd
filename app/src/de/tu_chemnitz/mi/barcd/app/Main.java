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
        Application app = null;

        try {
            app = new Application(options);
        } catch (ApplicationException ex) {
            printErrorAndExit(ex);
        }

        app.setExceptionHandler(new WorkerExceptionHandler() {
            @Override
            public void handleException(Worker worker, Exception exception) {
                printErrorAndExit(exception);
            }
        });

        app.run();
    }

    private static void printErrorAndExit(Exception exception) {
        Throwable cause = exception;
        while (cause != null) {
            System.err.println("[ERROR] " + cause.getMessage());
            cause = cause.getCause();
        }
        System.exit(1);
    }

    private static Options parseCommandLineArguments(String[] args) {
        CommandLineArgumentsParser parser = new CommandLineArgumentsParser();
        return parser.parse(args);
    }

    private static void printCopyrightNotice() {
        System.out.println(COPYRIGHT_NOTICE);
    }
}
