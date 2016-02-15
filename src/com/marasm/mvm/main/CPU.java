package com.marasm.mvm.main;

import com.marasm.ppc.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by sr3u on 19.08.15.
 */
public class CPU
{
    Memory mem;
    Stack<Variable> stack;
    Stack<Long> callStack;
    Program program;
    public long programcounter=0;
    public boolean debug=false;
    boolean halted=false;
    long interruptCalls=0;
    Queue<Variable>intQ=new LinkedList<>();//interrupts queue
    public CPU(Program p)
    {
        programcounter=0;
        mem= new Memory(p);
        program=p;
        stack=new Stack<>();
        callStack=new Stack<>();
        for(String fun : p.initializationFunctions)
            call(fun);
    }
    public boolean isHalted(){return halted;}

    void nop(){}
    void var(String v){mem.Allocate(v);}
    void gvar(String v){mem.gAllocate(v);}
    void delv(String v){mem.Deallocate(v);}
    void delg(String v){mem.gDeallocate(v);}
    void mov(String res,String v)
    {
        mem.Set(res, mem.getValue(v));
    }
    void add(String res,String v1,String v2)
    {
        mem.Set(res,mem.getValue(v1).add(mem.getValue(v2)));
    }
    void add()
    {
        Variable a=stack.pop();
        Variable b=stack.pop();
        stack.push(a.add(b));
    }
    void sub(String res,String v1,String v2)
    {
        mem.Set(res,mem.getValue(v1).sub(mem.getValue(v2)));
    }
    void sub()
    {
        Variable a=stack.pop();
        Variable b=stack.pop();
        stack.push(a.sub(b));
    }
    void mul(String res,String v1,String v2)
    {
        mem.Set(res,mem.getValue(v1).mul(mem.getValue(v2)));
    }
    void mul()
    {
        Variable a=stack.pop();
        Variable b=stack.pop();
        stack.push(a.mul(b));
    }
    void div(String res,String v1,String v2)
    {
        mem.Set(res,mem.getValue(v1).div(mem.getValue(v2)));
    }
    void div()
    {
        Variable a=stack.pop();
        Variable b=stack.pop();
        stack.push(a.div(b));
    }
    void floor(String res,String v)
    {
        mem.Set(res, mem.getValue(v).floor());
    }
    void floor(){stack.push(stack.pop().floor());}
    void ceil(String res,String v)
    {
        mem.Set(res, mem.getValue(v).ceil());
    }
    void ceil(){stack.push(stack.pop().ceil());}
    void push(String v)
    {
        stack.push(mem.getValue(v));
    }
    void pop(String res)
    {
        mem.Set(res, stack.pop());
    }
    void call(String fun)
    {
        callStack.push(programcounter);
        if(fun.startsWith("$")){programcounter=program.getFun(fun);}
        else {programcounter=mem.getValue(fun).longValue();}
        mem.push();
        if(interruptCalls>0){interruptCalls++;}
    }
    void ret()
    {
        mem.pop();
        programcounter=callStack.pop();
        programcounter++;
        if(interruptCalls>0){interruptCalls--;}
    }
    void jmp(String tag)
    {
        if(tag.substring(0,1).equals("@")){
        programcounter=program.getTag(tag);return;}
        programcounter=mem.getValue(tag).longValue();
    }
    void jz(String v,String tag) {if(mem.getValue(v).isEqual(new Variable(0))){jmp(tag);}}
    void jnz(String v,String tag){if(!mem.getValue(v).isEqual(new Variable(0))){jmp(tag);}}
    void jmz(String v,String tag){if(mem.getValue(v).isBigger(new Variable(0))){jmp(tag);}}
    void jlz(String v,String tag){if(mem.getValue(v).isSmaller(new Variable(0))){jmp(tag);}}
    void in(String res,String port)
    {
        mem.Set(res, PPC.in(mem.getValue(port)));
    }
    void out(String port,String data)
    {
        PPC.out(mem.getValue(port), mem.getValue(data));
    }
    void setint(String _int,String fun){InterruptsController.SetInterruptHandler(mem.getValue(_int), fun);}
    void _int(String _int){InterruptsController.Interrupt(mem.getValue(_int));}

    void rmint(String _int){InterruptsController.RemoveInterruptHandler(mem.getValue(_int));}
    void sleep(String v)
    {//TODO make sleep work with interrupts
        Log.warning("sleep is not properly implemented yed");
        try {Thread.sleep(mem.getValue(v).longValue());}
        catch (InterruptedException e){}
    }
    void halt(String v)
    {
        Console.println("halt with code: " + mem.getValue(v));
        halted=true;
        //System.exit(mem.getValue(v).intValue());
    }
    void load(String v,String addr)
    {
        mem.Set(v,RAM.load(mem.getValue(addr)));
    }
    void store(String addr,String v)
    {
        RAM.store(mem.getValue(addr),mem.getValue(v));
    }
    void trace(){if(debug){Log.trace(Trace());}}
    void log(String v)
    {
        if(debug)
        {
            Log.info(v);
        }
    }
    void print(String[] v)
    {
        if(debug)
        {
            for(String str : v)
            {
                Log.info(str+"="+mem.getValue(str));
            }
        }
    }
    String Trace()
    {
        String res=new String();
        res+="CMD: "+program.getCommand(programcounter)+"\n";
        res+="File: "+program.getFileName(programcounter)+"\n";
        res+="Line: "+(program.getLineInFile(programcounter)+1)+"\n";
        res+="Tags: " +program.tags.toString()+"\n";
        res+="Functions: "+program.functions.toString()+"\n";
        res+="Call stack: size="+callStack.size()+"\n";
        res+="stack: "+stack.toString()+"\n";
        for(Long pc : callStack)
        {
            Command cmd=program.getCommand(pc);
            if(cmd.args.length>0)
                res+=cmd.args[0]+"\n";
        }res+="\n";
        res+="tags: " +program.tags.toString()+"\n";
        res+="functions: "+program.functions.toString()+"\n";
        res+="Variables:\n"+mem.toString()+"\n";
        res+="Modules loaded: "+program.filesLoaded.toString()+"\n";
        res+=RAM.string();
        return res;
    }
    void exec(Command cmd)
    {
        if(cmd.name.length()==0){return;}
        if(cmd.name.startsWith("$")){return;}
        if(cmd.name.startsWith("@")){return;}
        try {
            switch (cmd.name) {
                case "nop":
                    nop();
                    break;
                case "="://mov
                    mov(cmd.args[0], cmd.args[1]);
                    break;
                case "+"://add
                    if(cmd.args.length>0){add(cmd.args[0], cmd.args[1], cmd.args[2]);}
                    else{add();}
                    break;
                case "-"://sub
                    if(cmd.args.length>0){sub(cmd.args[0], cmd.args[1], cmd.args[2]);}
                    else{sub();}
                    break;
                case "*"://mul
                    if(cmd.args.length>0){mul(cmd.args[0], cmd.args[1], cmd.args[2]);}
                    else{mul();}
                    break;
                case "/"://div
                    if(cmd.args.length>0){div(cmd.args[0], cmd.args[1], cmd.args[2]);}
                    else{div();}
                    break;
                case "floor":
                    if(cmd.args.length>0){floor(cmd.args[0], cmd.args[1]);}
                    else{floor();}
                    break;
                case "ceil":
                    if(cmd.args.length>0){ceil(cmd.args[0], cmd.args[1]);}
                    else{ceil();}
                    break;
                case "push":
                    push(cmd.args[0]);
                    break;
                case "pop":
                    pop(cmd.args[0]);
                    break;
                case "call":
                    call(cmd.args[0]);
                    break;
                case "ret":
                    ret();
                    break;
                case "jmp":
                    jmp(cmd.args[0]);
                    break;
                case "jz":
                    jz(cmd.args[0], cmd.args[1]);
                    break;
                case "jnz":
                    jnz(cmd.args[0], cmd.args[1]);
                    break;
                case "jmz":
                    jmz(cmd.args[0], cmd.args[1]);
                    break;
                case "jlz":
                    jlz(cmd.args[0], cmd.args[1]);
                    break;
                case "in":
                    in(cmd.args[0], cmd.args[1]);
                    break;
                case "out":
                    out(cmd.args[0], cmd.args[1]);
                    break;
                case "var":
                    var(cmd.args[0]);
                    break;
                case "gvar":
                    gvar(cmd.args[0]);
                    break;
                case "delv":
                    delv(cmd.args[0]);
                    break;
                case "delg":
                    delg(cmd.args[0]);
                    break;
                case "setint":
                    setint(cmd.args[0], cmd.args[1]);
                    break;
                case "int":
                    _int(cmd.args[0]);
                    break;
                case "rmint":
                    rmint(cmd.args[0]);
                    break;
                case "sleep":
                    sleep(cmd.args[0]);
                    break;
                case "halt":
                    halt(cmd.args[0]);
                    break;
                case "load":
                    load(cmd.args[0], cmd.args[1]);
                    break;
                case "store":
                    store(cmd.args[0], cmd.args[1]);
                    break;
                case "log":
                    log(String.join(" ", cmd.args));
                    break;
                case "print":
                    print(cmd.args);
                    break;
                case "trace":
                    trace();
                    break;
                default:
                    Log.error("illegal instruction '" + cmd.toString() + "'");
                    return;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            Log.error("Too few arguments for '"+cmd.name+"'");
        }
        processInterrupts();
    }
    void processInterrupts()
    {
        intQ.addAll(InterruptsController.pollIntQ());
        if(intQ.size()==0){return;}
        if(interruptCalls>0){return;}
        Variable v=intQ.poll();
        String handler=InterruptsController.GetInterruptHandler(v);
        if(handler!=null)
        {
            call(handler);
            interruptCalls++;
        }
    }
}
