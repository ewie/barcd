package de.tu_chemnitz.mi.barcd.app;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.cli2.validation.FileValidator;
import org.apache.commons.cli2.validation.InvalidArgumentException;
import org.apache.commons.cli2.validation.UrlValidator;
import org.apache.commons.cli2.validation.Validator;

import de.tu_chemnitz.mi.barcd.util.Range;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequence;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class CommandLineOptionsParser {
    private DefaultOption jobFileOption;

    private Argument templateArgument;

    private Argument tagArgument;

    private Argument paddingArgument;

    private Argument rangeArgument;

    private DefaultOption xmlSchemaOption;

    private Parser parser;

    public CommandLineOptionsParser() {
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
        options.setJobFile((File) commandLine.getValue(jobFileOption));
        options.setXmlSchemaUrl((URL) commandLine.getValue(xmlSchemaOption));
        options.setFrameUrlSequence(createFrameUrlSequence(commandLine));
        return options;
    }

    public TemplatedURLSequence createFrameUrlSequence(CommandLine commandLine) {
        String template = (String) commandLine.getValue(templateArgument);
        String tag = (String) commandLine.getValue(tagArgument);
        int padding = Integer.valueOf((String) commandLine.getValue(paddingArgument));
        String rangeString = (String) commandLine.getValue(rangeArgument);
        Range range = parseRange(rangeString);
        URL url;
        try {
            url = new File(template).toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        return new TemplatedURLSequence(url, tag, range, padding);
    }

    private Range parseRange(String s) {
        String[] ss = s.split(":", 3);
        switch (ss.length) {
        case 2:
            return new Range(Integer.valueOf(ss[0]), Integer.valueOf(ss[1]));
        case 3:
            return new Range(Integer.valueOf(ss[0]), Integer.valueOf(ss[1]),
                Integer.valueOf(ss[2]));
        default:
            throw new RuntimeException();
        }
    }

    /**
     * Create the command line parser.
     *
     * @return a command line parser
     */
    private Parser createParser() {
        DefaultOptionBuilder ob = new DefaultOptionBuilder();
        GroupBuilder gb = new GroupBuilder();
        ArgumentBuilder ab = new ArgumentBuilder();

        FileValidator fileValidator = new FileValidator();
        fileValidator.setReadable(true);
        fileValidator.setWritable(true);
        fileValidator.setExisting(true);
        fileValidator.setFile(true);

        Argument jobArgument = ab.withName("PATH")
                                 .withMinimum(1)
                                 .withMaximum(1)
                                 .withValidator(fileValidator)
                                 .create();

        templateArgument = ab.withName("TEMPLATE")
                             .withDescription("A templated URL identifying each frame result.")
                             .withMinimum(1)
                             .withMaximum(1)
                             .create();

        paddingArgument = ab.withName("PADDING")
                            .withDescription("The length of the sequence number padded with zeros to the left.")
                            .withMinimum(0)
                            .withMaximum(1)
                            .withDefault(0)
                            .create();

        tagArgument = ab.withName("TAG")
                        .withDescription("A unique substring in the templated URL that will be replaced by an integer from the given range.")
                        .withMinimum(1)
                        .withMaximum(1)
                        .create();

        jobFileOption = ob.withLongName("job")
                          .withDescription("The path to the job file.")
                          .withArgument(jobArgument)
                          .withRequired(true)
                          .create();

        Validator rangeValidator = new Validator() {
            @Override
            public void validate(List objects)
                throws InvalidArgumentException
            {
                Pattern pattern = Pattern.compile("^\\d+:\\d+(:-?\\d+)?$");
                for (Object object : objects) {
                    String range = (String) object;
                    Matcher matcher = pattern.matcher(range);
                    if (!matcher.matches()) {
                        throw new InvalidArgumentException("invalid range syntax" + range);
                    }
                }
            }
        };

        rangeArgument = ab.withName("RANGE")
                          .withDescription("The range of integers for which the template tag should be evaluated. Format: start:end[:step]")
                          .withMinimum(1)
                          .withMaximum(1)
                          .withValidator(rangeValidator)
                          .create();

        Group templateGroup = gb.withOption(templateArgument)
                                .withOption(tagArgument)
                                .withOption(rangeArgument)
                                .withOption(paddingArgument)
                                .create();

        DefaultOption framesOption = ob.withLongName("frames")
                                       .withChildren(templateGroup)
                                       .withRequired(true)
                                       .create();

        UrlValidator urlValidator = new UrlValidator();

        Argument xmlSchemaArgument = ab.withName("URL")
                                       .withMinimum(1)
                                       .withMaximum(1)
                                       .withValidator(urlValidator)
                                       .create();

        xmlSchemaOption = ob.withLongName("xml-schema")
                            .withShortName("xs")
                            .withDescription("Specify the location of the XML schema used for validation.")
                            .withArgument(xmlSchemaArgument)
                            .withRequired(false)
                            .create();

        DefaultOption helpOption = ob.withLongName("help")
                                     .withDescription("Display this help.")
                                     .create();

        Group group = gb.withOption(jobFileOption)
                        .withOption(framesOption)
                        .withOption(xmlSchemaOption)
                        .withOption(helpOption)
                        .create();

        HelpFormatter hf = new HelpFormatter();
        Parser p = new Parser();
        p.setGroup(group);
        p.setHelpFormatter(hf);
        p.setHelpOption(helpOption);
        return p;
    }
}
