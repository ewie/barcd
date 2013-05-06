/*
 * Copyright (c) 2012-2013 Erik Wienhold & René Richter
 * Licensed under the BSD 3-Clause License.
 */
package de.tu_chemnitz.mi.barcd.app;

import java.io.File;
import java.net.URL;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.builder.SwitchBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.option.Switch;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.validation.FileValidator;
import org.apache.commons.cli2.validation.UrlValidator;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class CommandLineArgumentsParser {
    private final Parser parser;

    public CommandLineArgumentsParser() {
        parser = createParser();
    }

    /**
     * @param args the arguments as passed to a main function
     *
     * @return the parsed options, null if the arguments could no be parsed
     */
    public Options parse(String[] args) {
        CommandLine commandLine = parser.parseAndHelp(args);
        if (commandLine == null) {
            return null;
        }
        Options options = new Options();
        options.setJobFile((File) commandLine.getValue("--job"));
        options.setXmlSchemaUrl((URL) commandLine.getValue("--xml-schema"));
        options.setDisplay(commandLine.getSwitch("+display"));
        options.setPersist(commandLine.getSwitch("+persist"));
        return options;
    }

    /**
     * Create the command line parser.
     *
     * @return a command line parser
     */
    private Parser createParser() {
        GroupBuilder gb = new GroupBuilder();

        Parser parser = new Parser();

        DefaultOption helpOption = createHelpOption();
        parser.setHelpOption(helpOption);

        Group group =
            gb.withOption(createJobFileOption())
              .withOption(createXmlSchemaOption())
              .withOption(createDisplaySwitch())
              .withOption(createPersistSwitch())
              .withOption(helpOption)
              .create();
        parser.setGroup(group);

        HelpFormatter hf = new HelpFormatter();
        parser.setHelpFormatter(hf);

        return parser;
    }

    private DefaultOption createHelpOption() {
        DefaultOptionBuilder ob = new DefaultOptionBuilder();

        DefaultOption helpOption =
            ob.withLongName("help")
              .withDescription("Display this help.")
              .create();

        return helpOption;
    }

    private DefaultOption createJobFileOption() {
        ArgumentBuilder ab = new ArgumentBuilder();
        DefaultOptionBuilder ob = new DefaultOptionBuilder();

        FileValidator fileValidator = new FileValidator();
        fileValidator.setReadable(true);
        fileValidator.setWritable(true);
        fileValidator.setExisting(true);
        fileValidator.setFile(true);

        Argument jobArgument =
            ab.withName("PATH")
              .withMinimum(1)
              .withMaximum(1)
              .withValidator(fileValidator)
              .create();

        DefaultOption jobFileOption =
            ob.withLongName("job")
              .withDescription("The path to the job file.")
              .withArgument(jobArgument)
              .withRequired(true)
              .create();

        return jobFileOption;
    }

    private DefaultOption createXmlSchemaOption() {
        ArgumentBuilder ab = new ArgumentBuilder();
        DefaultOptionBuilder ob = new DefaultOptionBuilder();

        UrlValidator urlValidator = new UrlValidator();

        Argument xmlSchemaArgument =
            ab.withName("URL")
              .withMinimum(1)
              .withMaximum(1)
              .withValidator(urlValidator)
              .create();

        DefaultOption xmlSchemaOption =
            ob.withLongName("xml-schema")
              .withShortName("xs")
              .withDescription("Specify the location of the XML schema used for validation.")
              .withArgument(xmlSchemaArgument)
              .withRequired(false)
              .create();

        return xmlSchemaOption;
    }

    private Switch createDisplaySwitch() {
        SwitchBuilder sb = new SwitchBuilder();
        Switch displaySwitch =
            sb.withName("display")
              .withDescription("Whether to display each frame.")
              .withSwitchDefault(false)
              .create();
        return displaySwitch;
    }

    private Switch createPersistSwitch() {
        SwitchBuilder sb = new SwitchBuilder();
        Switch persistSwitch =
            sb.withName("persist")
              .withDescription("Whether to persist any extractions")
              .withSwitchDefault(true)
              .create();
        return persistSwitch;
    }
}
