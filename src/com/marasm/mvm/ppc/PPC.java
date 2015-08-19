package com.marasm.mvm.ppc;

import java.util.Map;

/**
 * Created by sr3u on 19.08.15.
 */
public class PPC
{
    static Map<Variable,PPCDevice> devices;

    static public void connect(Variable port,PPCDevice dev)
    {
        devices.put(port,dev);
    }
    static public void disconnect(Variable port)
    {
        devices.remove(port);
    }
    public static void out(Variable port,Variable data)
    {
        PPCDevice dev=devices.get(port);
        if(dev!=null){dev.out(port,data);}
    }
    public static Variable in(Variable port)
    {
        PPCDevice dev=devices.get(port);
        if(dev==null){return new Variable();}
        Variable v=dev.in(port);
        if(v==null){return new Variable();}
        return v;
    }
}
