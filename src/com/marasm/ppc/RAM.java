package com.marasm.ppc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sr3u on 01.09.15.
 */
public class RAM
{
    private Map<String,Variable> ram= new HashMap<>();
    private static RAM instance=new RAM();
    private RAM(){}
    public static RAM getInstance(){return instance;}
    public String toString()
    {
        String res=new String();
        res+="RAM:\n";
        for(Map.Entry<String, Variable> entry : ram.entrySet())
        {res+="\t"+entry.getKey().toString()+" : "+entry.getValue().toString()+"\n";}
        res+="\n";
        return res;
    }
    private void allocate(Variable addr)
    {
        ram.put(addr.toString(),new Variable());
    }
    public Variable _load (Variable addr)
    {
        Variable res;
        res=ram.get(addr.toString());
        if(res==null){
            res=new Variable();
            allocate(addr);
        }
        else{res=new Variable(res);}
        return res;
    }
    public void _store (Variable addr,Variable value)
    {
       ram.put(addr.toString(),new Variable(value));
    }
    public static Variable load (Variable addr){return instance._load(addr);}
    public static void store (Variable addr,Variable value){instance._store(addr,value);}
    public static String string()
    {
        return instance.toString();
    }
}
