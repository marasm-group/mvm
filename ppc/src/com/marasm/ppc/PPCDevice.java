package com.marasm.ppc;

/**
 * Created by sr3u on 19.08.15.
 */
public class PPCDevice
{
    public void connected(){}
    public void out(Variable port,Variable data){}
    public Variable in(Variable port){return new Variable();}
    public String manufacturer(){return getClass().getPackage().getImplementationVendor();}
}
