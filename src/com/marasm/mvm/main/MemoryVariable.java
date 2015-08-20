package com.marasm.mvm.main;

import com.marasm.ppc.Log;
import com.marasm.ppc.Variable;

/**
 * Created by sr3u on 19.08.15.
 */
public class MemoryVariable
{
    int arraySize;
    Variable[] vars;
    MemoryVariable(){this(1);}
    MemoryVariable(int size)
    {
        arraySize=size;
        vars=new Variable[size];
        for(int i=0;i<vars.length;i++)
        {
            vars[i]=new Variable();
        }
    }
    Variable get(){return vars[0];}
    Variable get(int idx)
    {
        if(idx>arraySize){
            Log.error("Index " + idx + " is out of bounds!");return vars[0];}
        return vars[idx];
    }
    void set(Variable v)
    {
        vars[0]=v;
    }
    void set(Variable v,int idx)
    {
        if(idx>arraySize){Log.error("Index "+idx+" is out of bounds!");return;}
        vars[idx]=v;
    }
    public String toString()
    {
        if(arraySize<2){return get().toString();}
        return vars.toString();
    }
}