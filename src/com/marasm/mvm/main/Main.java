package com.marasm.mvm.main;

import com.marasm.ppc.*;
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
        options.addOption("mvmHome",true,"set custom mvm home directory");
        options.addOption("debugPort",true,"port to listen for remote debugger");
        options.addOption("cppOut",true,"c++ output file (if this option si set, mvm will not execute program)");
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
        if(cmd.hasOption("mvmHome"))
        {
            Utils.setMarasmHome(cmd.getOptionValue("mvmHome"));
        }
        instance=new Main();

        Log.setErrorHandler(instance);
        if(cmd.hasOption("D"))
        {
            debug=true;
        }
        String cppout=null;
        if(cmd.hasOption("cppOut"))
        {
            cppout=cmd.getOptionValue("cppOut");
        }
        if(cmd.hasOption("D"))
        {
            debug=true;
        }
        if(cmd.hasOption("e"))
        {
            execute(cmd.getOptionValue("e"),cppout);
        }
        else {
            if(cmd.getArgList().size()==0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("mvm", options);
                System.exit(0);
            }
            else {
                execute(cmd.getArgs()[0],cppout);
            }
        }

    }
    static void execute(String path,String cppOut)
    {
        Log.info("Working directory: " + Utils.workingDir());
        Log.info("User home directory: " + Utils.homeDir());
        Log.info("Marasm home: " + Utils.marasmHome());
        Log.info("Marasm modules: " + Utils.marasmModules());
        Log.info("Marasm devices: " + Utils.marasmDevices());
        PPC.LoadDevices(Utils.marasmDevices());
        Program p=new Program(path);
        if(cppOut==null){cpu=new CPU(p);}
        else{cpu=new CPUCPP(p,cppOut);}
        cpu.debug=debug;
        while (cpu.programcounter<cpu.program.size()&&!cpu.isHalted())
        {
            long oldPC=cpu.programcounter;
            cpu.exec(cpu.program.getCommand(cpu.programcounter));
            if(cpu.programcounter==oldPC){cpu.programcounter++;}
        }
        if(cppOut!=null)
        {
            cpu.end();
            cpu.flush();
            System.out.println("Compiled to "+cppOut);
            System.exit(0);
        }
        System.out.println("press return key to exit or just kill this process");
        try {System.in.read();}
        catch (IOException e) {e.printStackTrace();}
    }

    public void error()
    {
        if(cpu!=null){Log.info(cpu.Trace());cpu.halt("-1");}
        System.out.println("press return key to exit or just kill this process");
        try {System.in.read();}
        catch (IOException e) {e.printStackTrace();}
        System.exit(-1);
    }
    public void warning(){}
    Main(){}
}
