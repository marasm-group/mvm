package com.marasm.mvm;

import com.marasm.mvm.ppc.ErrorHandler;
import com.marasm.mvm.ppc.Log;
import org.apache.commons.cli.*;

public class Main implements ErrorHandler {
    static CPU cpu;
    static Main instance;
    Main(){}
    public static void main(String[] args) {
        Options options=new Options();
        options.addOption("e",true,"marASM executable file");
        options.addOption("h",false,"print help");
        options.addOption("D",false,"enable debug instructions");
        options.addOption("debugPort",true,"port to listen for remote debugger");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(cmd.hasOption("h"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("mvm", options);
            System.exit(0);
        }
        instance=new Main();

        Log.setErrorHandler(instance);
        Log.info("Working directory: " + Utils.workingDir());
        Log.info("User home directory: " + Utils.homeDir());
        Log.info("Marasm home: " + Utils.marasmHome());
        Log.info("Marasm modules: " + Utils.marasmModules());
        Log.info("Marasm devices: " + Utils.marasmDevices());
        if(cmd.hasOption("e"))
        {
            execute(cmd.getOptionValue("e"));
        }
        else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "mvm", options );
            System.exit(0);
        }

    }
    static void execute(String path)
    {
        Program p=new Program(path);
        cpu=new CPU(p);
        while (cpu.programcounter<cpu.program.size())
        {
            long oldPC=cpu.programcounter;
            cpu.exec(cpu.program.getCommand(cpu.programcounter));
            if(cpu.programcounter==oldPC){cpu.programcounter++;}

        }
    }

    public void error()
    {
        if(cpu!=null){cpu.Trace();cpu.halt("-1");}
        else{System.exit(-1);}
    }
    public void warning(){}
}
