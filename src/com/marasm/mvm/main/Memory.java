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
    Map<String,Variable> vars;//local Variables
    Map<String,Variable> gvars;//global Variables
    Stack<Map<String,Variable>>varsStack;

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
        for (Map.Entry<String, Variable> mv : vars.entrySet())
            res+="\t"+mv.getKey()+"="+mv.getValue().toString()+"\n";
        res+="Global:\n";
        for (Map.Entry<String, Variable> mv : gvars.entrySet())
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
    private boolean checkVar(String varname,Map<String,Variable> container)
    {
        if(container.get(varname)!=null){return true;}
        if(container.get(varname+"[0]")!=null){return true;}
        int idx = varname.indexOf("[");
        if (idx== -1){return false;}
        if(container.get(varname.substring(0,idx))!=null){return true;}
        return false;
    }
    public void Allocate(String varname)
    {
        if(checkVar(varname,vars)){Log.error("Variable '" + varname.split("\\[")[0] + "' already exists!");return;}
        Allocate(varname,vars);
    }
    public void gAllocate(String varname){
        if(checkVar(varname,gvars)){Log.error("Variable '" + varname.split("\\[")[0] + "' already exists!");return;}
        if(checkVar(varname,vars)){Log.error("Variable '" + varname.split("\\[")[0] + "' already exists!");return;}
        Allocate(varname, gvars);

    }
    private void Allocate(String varname,Map<String,Variable>container)
    {
        int idx=varname.indexOf("[");
        if(idx==-1){container.put(varname,new Variable());return;}
        int as=ArraySize(varname);
        if(as==0){Log.error("Array size cannot be 0");}
        varname=varname.substring(0,idx);
        for(int i=0;i<as;i++)
        {
            container.put(varname+"["+i+"]",new Variable());
        }
    }
    public void Deallocate(String varname){Deallocate(varname, vars);}
    public void gDeallocate(String varname){Deallocate(varname, gvars);}
    private void Deallocate(String varname,Map<String,Variable>container)
    {
        if(container.get(varname)==null){
            if(container.get(varname+"["+0+"]")==null)
            {
                Log.error("Variable'"+varname+"' does not exist!");return;
            }
            int i=0;
            while (true)
            {
                String tmp=varname+"["+i+"]";
                if(container.get(tmp)==null){return;}
                container.remove(tmp);
                i++;
            }
        }
        container.remove(varname);
    }
    public Variable Get(String varname)
    {
        Variable v=vars.get(varname);
        if(v!=null){return new Variable(v);}
        v=gvars.get(varname);
        if(v!=null){return new Variable(v);}
        Log.error("Variable'" + varname + "' does not exist!");
        return null;
    }
    public void Set(String varname,Variable val)
    {
        Variable v=vars.get(varname);
        if(v!=null)
        {
            v.set(val);
            return;
        }
        v=gvars.get(varname);
        if(v!=null)
        {
            v.set(val);
            return;
        }
        Log.error("Variable '"+varname+"' does not exist!");
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
