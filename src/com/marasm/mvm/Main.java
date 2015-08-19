package com.marasm.mvm;

import com.marasm.mvm.ppc.ErrorHandler;
import com.marasm.mvm.ppc.Log;

public class Main implements ErrorHandler {
    static CPU cpu;
    static Main instance;
    Main(){}
    public static void main(String[] args) {
        instance=new Main();
        Log.setErrorHandler(instance);
        Log.info("Working directory: " + Utils.workingDir());
        Log.info("User home directory: " + Utils.homeDir());
        Log.info("Marasm home: " + Utils.marasmHome());
        Log.info("Marasm modules: " + Utils.marasmModules());
        Log.info("Marasm devices: " + Utils.marasmDevices());
        execute("/Users/sr3u/test.marasm");
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
