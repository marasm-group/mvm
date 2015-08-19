package com.marasm.mvm.ppc;

/**
 * Created by sr3u on 19.08.15.
 */
public interface PPCDevice
{
    public void connected();
    public void out(Variable port,Variable data);
    public Variable in(Variable port);
}
