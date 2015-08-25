package com.marasm.mvm.main;

import com.marasm.ppc.ErrorHandler;
import com.marasm.ppc.Log;
import com.marasm.ppc.PPC;
import org.apache.commons.cli.*;

import java.io.IOException;

public class Main implements ErrorHandler {
    static CPU cpu;
    static Main instance;
    static boolean debug=true;
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
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("mvm", options);
            System.exit(127);
        }
        if(cmd.hasOption("h"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("mvm", options);
            System.exit(0);
        }
        instance=new Main();

        Log.setErrorHandler(instance);
        if(cmd.hasOption("D"))
        {
            debug=true;
        }
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
        Log.info("Working directory: " + Utils.workingDir());
        Log.info("User home directory: " + Utils.homeDir());
        Log.info("Marasm home: " + Utils.marasmHome());
        Log.info("Marasm modules: " + Utils.marasmModules());
        Log.info("Marasm devices: " + Utils.marasmDevices());
        PPC.LoadDevices(Utils.marasmDevices());
        Program p=new Program(path);
        cpu=new CPU(p);
        cpu.debug=debug;
        while (cpu.programcounter<cpu.program.size()&&!cpu.isHalted())
        {
            long oldPC=cpu.programcounter;
            cpu.exec(cpu.program.getCommand(cpu.programcounter));
            if(cpu.programcounter==oldPC){cpu.programcounter++;}
        }
        System.out.println("press return key to exit or just kill this process");
        try {System.in.read();}
        catch (IOException e) {e.printStackTrace();}
    }

    public void error()
    {
        if(cpu!=null){Log.info(cpu.Trace());cpu.halt("-1");System.exit(-1);}
        else{System.exit(-1);}
    }
    public void warning(){}
    Main(){}
}
