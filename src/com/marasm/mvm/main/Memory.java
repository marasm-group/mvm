package com.marasm.mvm.main;

import com.marasm.ppc.Log;
import com.marasm.ppc.Variable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by sr3u on 19.08.15.
 */
public class Memory
{
    Map<String,MemoryVariable> vars;//local MemoryVariables
    Map<String,MemoryVariable> gvars;//global MemoryVariables
    Stack<Map<String,MemoryVariable>>varsStack;

    public Memory()
    {
        vars=new HashMap<>();
        gvars=new HashMap<>();
        varsStack=new Stack<>();
    }
    public String toString()
    {
        String res=new String();
        res+="Local:\n";
        for (Map.Entry<String, MemoryVariable> mv : vars.entrySet())
            res+="\t"+mv.getKey()+"="+mv.getValue().toString()+"\n";
        res+="Global:\n";
        for (Map.Entry<String, MemoryVariable> mv : gvars.entrySet())
            res+="\t"+mv.getKey()+"="+mv.getValue().toString()+"\n";
        return res;
    }
    public void push()
    {
        varsStack.push(vars);
        vars=new HashMap<>();
    }
    public void pop()
    {
        vars=varsStack.pop();
    }
    int ArraySize(String varname)
    {
        int idx = varname.indexOf("[");
        if (idx== -1){return 0;}
        String ars="0";
        ars=varname.substring(idx);
        idx = varname.lastIndexOf("]");
        if (idx== -1){
            Log.error("No ']' found for '['");return 0;}
        ars=ars.substring(0,idx).trim();
        try {
            ars=ars.replace("[","");
            ars=ars.replace("]","");
            return Integer.parseInt(ars);
        }
        catch (NumberFormatException e) {
            return Get(ars).intValue();
        }
    }
    public void Allocate(String varname)
    {
        if(vars.get(varname)!=null){Log.error("Variable'" + varname + "' already exists!");return;}
        int idx=varname.indexOf("[");
        if(idx==-1){vars.put(varname,new MemoryVariable());return;}
        int as=ArraySize(varname);
        if(as==0){Log.error("Array size cannot be 0");}
        varname=varname.substring(0,idx);
        vars.put(varname,new MemoryVariable(as));
    }
    public void gAllocate(String varname){
        if(vars.get(varname)!=null){Log.error("Variable'" + varname + "' already exists!");return;}
        if(gvars.get(varname)!=null){Log.error("Variable'" + varname + "' already exists!");return;}
        int idx=varname.indexOf("[");
        if(idx==-1){gvars.put(varname,new MemoryVariable());return;}
        int as=ArraySize(varname);
        if(as==0){Log.error("Array size cannot be 0");}
        varname=varname.substring(0,idx);
        gvars.put(varname,new MemoryVariable(as));
    }
    private void Allocate(String varname,Map<String,MemoryVariable>container)
    {

    }
    public void Deallocate(String varname){Deallocate(varname, vars);}
    public void gDeallocate(String varname){Deallocate(varname, gvars);}
    private void Deallocate(String varname,Map<String,MemoryVariable>container)
    {
        if(container.get(varname)==null){Log.error("Variable'"+varname+"' does not exist!");return;}
        container.remove(varname);
    }
    public Variable Get(String varname)
    {
        int idx=varname.indexOf("[");
        if(idx==-1){
            MemoryVariable v=vars.get(varname);
            if(v!=null){return v.get();}
            v=gvars.get(varname);
            if(v!=null){return v.get();}
            Log.error("Variable'" + varname + "' does not exist!");
            idx=0;
        }
        int as=ArraySize(varname);
        String oldVarName=varname;
        varname=varname.substring(0,idx);
        MemoryVariable v=vars.get(varname);
        if(v!=null){return v.get(as);}
        v=gvars.get(varname);
        if(v!=null){return v.get(as);}
        Log.error("Variable'" + oldVarName + "' does not exist!");
        return null;
    }
    public void Set(String varname,Variable val)
    {
        int idx=varname.indexOf("[");
        int as=0;
        String oldVarName=varname;
        if(idx!=-1){as=ArraySize(varname);varname=varname.substring(0,idx);}
        MemoryVariable v=vars.get(varname);
        if(v!=null)
        {
            v.set(val,as);
            return;
        }
        v=gvars.get(varname);
        if(v!=null)
        {
            v.set(val, as);
            return;
        }
        Log.error("Variable '"+oldVarName+"' does not exist!");
        return;
    }
    public Variable getValue(String str)
    {
        try
        {
            BigDecimal bd=new BigDecimal(str);
            return new Variable(bd);
        }
        catch (NumberFormatException e)
        {
            if(str.substring(0,1).equals("'"))
            {
                str=Utils.unescape(str);
                return Variable.Character(str);
            }
            return Get(str);
        }
    }
}
