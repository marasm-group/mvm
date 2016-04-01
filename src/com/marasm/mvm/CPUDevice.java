package com.marasm.mvm;

import com.marasm.ppc.PPC;
import com.marasm.ppc.PPCDevice;
import com.marasm.ppc.Variable;

/**
 * Created by vhq473 on 01.04.2016.
 */
public class CPUDevice extends PPCDevice
{
    final String ctrlPort="0";
    final String stackPort="0.1";
    public CPU cpu;
    public String manufacturer(){return "marasm.mvm";}
    public CPUDevice(CPU cpu)
    {
        this.cpu=cpu;
        PPC.connect(new Variable(ctrlPort),this);
        PPC.connect(new Variable(stackPort),this);
    }
    public void out(Variable port, Variable data)
    {
        switch (port.toString())
        {
            case ctrlPort:
                ctrlOut(data);
                break;
            default:break;
        }
    }
    public Variable in(Variable port)
    {
        switch (port.toString())
        {
            case ctrlPort:
                return ctrlIn();
            case stackPort:
                if(cpu==null){return new Variable(0);}
                return new Variable(cpu.callStack.size());
            default:break;
        }
        return new Variable(0);
    }
}
