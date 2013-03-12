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
import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequence;

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
        options.setFrameUrlSequence(createFrameUrlSequence(commandLine));
        return options;
    }

    public TemplatedUrlSequence createFrameUrlSequence(CommandLine commandLine) {
        String template = (String) commandLine.getValue("TEMPLATE");
        String tag = (String) commandLine.getValue("TAG");
        int padding = Integer.valueOf((String) commandLine.getValue("PADDING"));
        String rangeString = (String) commandLine.getValue("RANGE");
        Range range = parseRange(rangeString);
        URL url;
        try {
            url = new File(template).toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        return new TemplatedUrlSequence(url, tag, range, padding);
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
        GroupBuilder gb = new GroupBuilder();

        Parser parser = new Parser();

        DefaultOption helpOption = createHelpOption();
        parser.setHelpOption(helpOption);

        Group group =
            gb.withOption(createJobFileOption())
              .withOption(createFramesOption())
              .withOption(createXmlSchemaOption())
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

    private DefaultOption createFramesOption() {
        ArgumentBuilder ab = new ArgumentBuilder();
        DefaultOptionBuilder ob = new DefaultOptionBuilder();
        GroupBuilder gb = new GroupBuilder();

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

        Argument templateArgument =
            ab.withName("TEMPLATE")
              .withDescription("A templated URL identifying each frame result.")
              .withMinimum(1)
              .withMaximum(1)
              .create();

        Argument paddingArgument =
            ab.withName("PADDING")
              .withDescription("The length of the sequence number padded with zeros to the left.")
              .withMinimum(0)
              .withMaximum(1)
              .withDefault(0)
              .create();

        Argument tagArgument =
            ab.withName("TAG")
              .withDescription("A unique substring in the templated URL that will be replaced by an integer from the given range.")
              .withMinimum(1)
              .withMaximum(1)
              .create();

        Argument rangeArgument =
            ab.withName("RANGE")
              .withDescription("The range of integers for which the template tag should be evaluated. Format: start:end[:step]")
              .withMinimum(1)
              .withMaximum(1)
              .withValidator(rangeValidator)
              .create();

        Group templateGroup =
            gb.withOption(templateArgument)
              .withOption(tagArgument)
              .withOption(rangeArgument)
              .withOption(paddingArgument)
              .create();

        DefaultOption framesOption =
            ob.withLongName("frames")
              .withChildren(templateGroup)
              .withRequired(true)
              .create();

        return framesOption;
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
}
