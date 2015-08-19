package com.marasm.mvm;

import com.marasm.mvm.ppc.Variable;

public class Main
{

    public static void main(String[] args)
    {
        Log.info("PWD: "+Utils.workingDir());
        Log.info("Home: " + Utils.marasmHome());
        Log.info("Modules: " + Utils.marasmModules());
        Log.info("Devices: " + Utils.marasmDevices());
        Memory m=new Memory();
        m.Allocate("x[5]");
        Log.info(m);
        m.Set("x", new Variable(10));
        m.Set("x[3]", new Variable("100.1"));
        Log.info(m.Get("x"));
        Log.info(m.Get("x[3]"));
    }
}
