package com.marasm.mvm;

import com.marasm.ppc.*;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by vhq473 on 31.03.2016.
 */
public class ProfileCPU extends CPU
{
    class FunDuration
    {
        String fun;
        long startTime=0;
        FunDuration(String fun)
        {
            this.fun=fun;
            startTime=System.currentTimeMillis();
        }
        public long end()
        {
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            return elapsedTime;
        }
    }
    public ProfileCPU(Program p)
    {
        super(p);
    }
    Map<String,Long> instrCounts;
    Map<String,Long> funCallsCounts;
    Map<String,Long> tagJmpCounts;
    Map<String,Long> portsIn;
    Map<String,Long> portsOut;
    Map<String,Double> avgFunTimes;
    long varAllocations=0;
    long gvarAllocations=0;
    long varDeletions=0;
    long gvarDeletions=0;
    long maxStackSize=0;
    long maxCallStack=0;
    long debugInstructions=0;
    long ramIOs=0;
    long emptyCommands=0;
    Variable haltCode=new Variable("0");

    Stack<FunDuration> funDurationStack;
    void incrementCounter(Map<String,Long>m,String key)
    {
        Long l=m.get(key);
        if(l!=null)
        {
            l++;
            m.put(key,l);
        }
        else
        {
            m.put(key,new Long(1));
        }
    }
    void appendMeasurement(Map<String,Double>m,String key,Long value)
    {
        Double l=m.get(key);
        if(l==null){l=0.0;}
        l=l+value;
        m.put(key,l);
    }
    public void exec(Command cmd)
    {
        super.exec(cmd);
        if(cmd.name.length()==0)
        {
            emptyCommands++;
            return;
        }
        if(cmd.name.startsWith("$")){return;}
        if(cmd.name.startsWith("@")){return;}
        if(instrCounts==null){instrCounts=new HashMap<>();}
        incrementCounter(instrCounts,cmd.fullName);
    }

    public void var(String v)
    {
        varAllocations++;
        super.var(v);
    }
    public void gvar(String v)
    {
        gvarAllocations++;
        super.gvar(v);
    }
    public void delv(String v)
    {
        varDeletions++;
        super.delv(v);
    }
    public void delg(String v)
    {
        gvarDeletions++;
        super.delg(v);
    }

    public void push(String v)
    {
        maxStackSize=Math.max(stack.size(),maxStackSize);
        super.push(v);
    }
    public void call(String fun)
    {
        maxCallStack= Math.max(maxCallStack,callStack.size());
        if(funCallsCounts==null){funCallsCounts=new HashMap<>();}
        incrementCounter(funCallsCounts,fun);
        if(funDurationStack==null)
        {
            funDurationStack=new Stack<>();
        }
        funDurationStack.push(new FunDuration(fun));
        super.call(fun);
    }
    public void ret()
    {
        super.ret();
        FunDuration fd=funDurationStack.pop();
        if(avgFunTimes==null){avgFunTimes=new HashMap<>();}
        appendMeasurement(avgFunTimes,fd.fun,fd.end());
    }
    public void jmp(String tag)
    {
        if(tagJmpCounts==null){tagJmpCounts=new HashMap<>();}
        incrementCounter(tagJmpCounts,tag);
        super.jmp(tag);
    }
    public void halt(String v)
    {
        super.halt(v);
        haltCode=mem.getValue(v);
        saveReport();
    }
    public void load(String v,String addr)
    {
        ramIOs++;
        super.load(v,addr);
    }
    public void store(String addr,String v)
    {
        ramIOs++;
        super.store(addr,v);
    }
    public void trace()
    {
        debugInstructions++;
        super.trace();
    }
    public void log(String v)
    {
        debugInstructions++;
        super.log(v);
    }
    public void print(String[] v)
    {
        debugInstructions++;
        super.print(v);
    }
    public void in(String res,String port)
    {
        if(portsIn==null){portsIn=new HashMap<>();}
        incrementCounter(portsIn,mem.getValue(port).toString());
        super.in(res,port);
    }
    public void out(String port,String data)
    {
        if(portsOut==null){portsOut=new HashMap<>();}
        incrementCounter(portsOut,mem.getValue(port).toString());
        super.out(port,data);
    }
    //TODO
    public void setint(String _int,String fun)
    {
        super.setint(_int,fun);
    }
    public void _int(String _int)
    {
        super._int(_int);
    }
    public void rmint(String _int)
    {
        super.rmint(_int);
    }
    public void sleep(String v)
    {
        super.sleep(v);
    }
    private void prepareReport()
    {
        for (String k:avgFunTimes.keySet())
        {
            Double res=avgFunTimes.get(k);
            Long count=funCallsCounts.get(k);
            res=res/count;
            avgFunTimes.put(k,res);
        }
    }
    private void saveReport()
    {
        prepareReport();
        JSONObject profileReport=new JSONObject();
        profileReport.put("halt_code",haltCode.toString());
        profileReport.put("instructions",instrCounts);
        profileReport.put("calls",funCallsCounts);
        profileReport.put("jumps",tagJmpCounts);
        JSONObject var=new JSONObject();
        var.put("allocations",varAllocations);
        var.put("global_allocations",gvarAllocations);
        var.put("deletions",varDeletions);
        var.put("global_deletions",gvarDeletions);
        profileReport.put("variables",var);
        JSONObject pStack=new JSONObject();
        pStack.put("max_size",maxStackSize);
        pStack.put("max_calls",maxCallStack);
        profileReport.put("stack",pStack);
        profileReport.put("debug_instructions",debugInstructions);
        profileReport.put("RAM_IOs",ramIOs);
        profileReport.put("empty_commands",emptyCommands);
        JSONObject ports=new JSONObject();
        ports.put("in",portsIn);
        ports.put("out",portsOut);
        profileReport.put("ports",ports);
        //Save report
        profileReport.put("avg_fun_times_ms",avgFunTimes);
        String reportLocation=Utils.workingDir()+"/"+Utils.lastPathComponent(program.mainFileKey)+".json";
        System.out.println("profiling report saved to "+reportLocation);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(reportLocation, "UTF-8");
            writer.println(profileReport.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("failed to save profiling report:\n"+profileReport.toString());
        }
    }
    protected void finalize() throws Throwable {
        saveReport();
    }
}
