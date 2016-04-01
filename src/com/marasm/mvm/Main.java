package com.marasm.mvm;

import com.marasm.mvm.codegen.JavaCPU;
import com.marasm.ppc.ErrorHandler;
import com.marasm.ppc.Log;
import com.marasm.ppc.PPC;
import com.marasm.ppc.Variable;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main implements ErrorHandler {
    static CPU cpu;
    static Main instance;
    static boolean debug=true;
    static boolean profile=false;
    public static void main(String[] args) {
        Options options=new Options();
        options.addOption("e",true,"marASM executable file");
        options.addOption("h",false,"print help");
        options.addOption("D",false,"enable debug instructions");
        options.addOption("mvmHome",true,"set custom mvm home directory");
        options.addOption("debugPort",true,"port to listen for remote debugger");
        options.addOption("javaOut",true,"java output file (if this option si set, mvm will not execute program)");
        options.addOption("devicePort",true,"act like device server for other programs on selected port");
        options.addOption("profile",false,"run program profiling (WARNING: this will slow down execution)");
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
        String javaout=null;
        if(cmd.hasOption("javaOut"))
        {
            javaout=cmd.getOptionValue("javaOut");
        }
        if(cmd.hasOption("D"))
        {
            debug=true;
        }
        if(cmd.hasOption("devicePort"))
        {
            deviceServer(cmd.getOptionValue("devicePort"));
            System.exit(0);
        }
        if(cmd.hasOption("profile"))
        {
            profile=true;
        }
        if(cmd.hasOption("e"))
        {
            execute(cmd.getOptionValue("e"),javaout);
        }
        else {
            if(cmd.getArgList().size()==0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("mvm", options);
                System.exit(0);
            }
            else {
                execute(cmd.getArgs()[0],javaout);
            }
        }

    }
    public static void prepare(){prepare(true);}
    public static void prepare(boolean loadDevices)
    {
        Log.info("Working directory: " + Utils.workingDir());
        Log.info("User home directory: " + Utils.homeDir());
        Log.info("Marasm home: " + Utils.marasmHome());
        Log.info("Marasm modules: " + Utils.marasmModules());
        Log.info("Marasm devices: " + Utils.marasmDevices());
        if(loadDevices) {
            PPC.LoadDevices(Utils.marasmDevices());
        }
    }
    static void execute(String path,String javaOut)
    {
        prepare(javaOut==null);
        Program p=new Program(path);
        if(javaOut==null)
        {
            if(profile){cpu=new ProfileCPU(p);}
            else{cpu=new CPU(p);}
        }
        else{cpu=new JavaCPU(p,javaOut);}
        cpu.debug=debug;
        CPUDevice cpuDevice=new CPUDevice(cpu);
        while (cpu.programcounter<cpu.program.size()&&!cpu.isHalted())
        {
            long oldPC=cpu.programcounter;
            cpu.exec(cpu.program.getCommand(cpu.programcounter));
            if(cpu.programcounter==oldPC){cpu.programcounter++;}
        }
        if(javaOut!=null)
        {
            cpu.end();
            cpu.flush();
            System.out.println("Compiled to "+javaOut);
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
    static void deviceServer(String port)
    {
        prepare();
        ServerSocket serverSocket= null;
        try {
            serverSocket = new ServerSocket(Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true)
        {
            try
            {
                Socket server = serverSocket.accept();
                /*new Thread()
                {
                    public void run()
                    {
                        try{*/
                            BufferedReader in =new BufferedReader(new InputStreamReader(server.getInputStream()));
                            BufferedWriter out=new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
                            String cmd = in.readLine();
                            String[] s = cmd.split("\\:");
                            if (s.length == 0) {
                                continue;
                            }
                            if (!s[0].contains(".")) {
                                s[0] += ".0";
                            }
                            System.out.println(cmd);
                            switch (s.length) {
                                case 1:
                                    Variable res = PPC.in(new Variable(s[0]));
                                    out.write(res.toString());
                                    break;
                                case 2:
                                    PPC.out(new Variable(s[0]), new Variable(s[1]));
                                    out.write("OK\n");
                                    break;
                                default:
                                    break;
                            }
                            server.close();
                        /*}
                        catch(Exception e){e.printStackTrace();}
                    }
                }.start();*/
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
