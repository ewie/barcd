package de.tu_chemnitz.mi.barcd.app;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.util.HelpFormatter;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class CommandLineOptions {
    private CommandLine commandLine;
    
    private DefaultOption jobFilePathOption;
    
    /**
     * @param args
     * 
     * @return true if the command line arguments were successfully parsed
     */
    public boolean parse(String[] args) {
        Parser parser = createParser();
        commandLine = parser.parseAndHelp(args);
        return commandLine != null;
    }
    
    /**
     * Get the path of the job file.
     * 
     * @return the job file's path
     */
    public String getJobFilePath() {
        return (String) commandLine.getValue(jobFilePathOption);
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
        
        Argument jobArgument = ab.withName("PATH")
                                 .withMinimum(1)
                                 .withMaximum(1)
                                 .create();
        
        jobFilePathOption = ob.withLongName("job")
                              .withArgument(jobArgument)
                              .withRequired(true)
                              .create();
        
        Group group = gb.withOption(jobFilePathOption).create();
        
        HelpFormatter hf = new HelpFormatter();
        Parser p = new Parser();
        p.setGroup(group);
        p.setHelpFormatter(hf);
        p.setHelpTrigger("help");
        return p;
    }
}
