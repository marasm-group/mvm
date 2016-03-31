package com.marasm.ppc;

import java.util.*;

/**
 * Created by sr3u on 19.08.15.
 */
public class InterruptsController
{

    static Queue<Variable>intQ=new LinkedList<>();//interrupts queue
    static Map<String,String>handlers=new HashMap<>();
    //static Stack<Variable> params=new Stack<>();

    public static final Variable int_Arithmetic = new Variable(0);

    public static void Interrupt(Variable _int)
    {synchronized (intQ){
        if(handlers.get(_int.toString())==null){return;}
        intQ.add(_int);
    }}
    public static void SetInterruptHandler(Variable _int,String handler)
    {
        if(handlers.get(_int.toString())!=null){Log.warning("Replacing handler for interrupt "+_int);}
        handlers.put(_int.toString(), handler);
    }
    public static String GetInterruptHandler(Variable _int)
    {
        return handlers.get(_int.toString());
    }
    public static void RemoveInterruptHandler(Variable _int)
    {
        if(handlers.get(_int.toString())!=null)
        {
            handlers.remove(_int.toString());
        }
    }
    //public static void push(Variable v){params.push(v);}
    //public static Variable pop(Variable v){return params.pop();}
    //public static Stack<Variable> getParams(){return params;}
    //public static void emptyParams(){params=new Stack<>();}
    public static Queue<Variable> pollIntQ()
    {synchronized (intQ){
        Queue<Variable>tmp=getIntQ();
        emptyIntQ();
        return tmp;
    }}
    public static Queue<Variable> getIntQ(){return intQ;}
    public static void emptyIntQ(){intQ=new LinkedList<>();}
}
