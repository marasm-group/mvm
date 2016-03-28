package com.marasm.mvm.main;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by vhq473 on 09.03.2016.
 */
public class JavaCPU extends CPU {
    FileWriter file;
    ArrayList<String> variables = new ArrayList<>();
    static String tagPrefix = "__at_";
    static String funPrefix = "__fun_";
    static String retPrefix = "__ret_";
    static String illegalPrefix = "__illegal_";
    long initCount=0;
    ArrayList<String> tags;
    ArrayList<String> functions;
    ArrayList<String> returns;
    ArrayList<String> illegals;

    public JavaCPU(Program p, String outputFile) {
        super(p);
        try {
            file = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tags = new ArrayList<>();
        functions = new ArrayList<>();
        returns = new ArrayList<>();
        illegals = new ArrayList<>();
        initCount=0;
        write(header);
        for (String fun : program.initializationFunctions) {
            call(fun);
            initCount++;
        }
        initCount=-1;
    }

    String varFormat(String str) {
        return "\"" + str + "\"";
    }

    public void end(){}

    boolean prevBreak=false;
    public void write(String str)
    {
        if(str.startsWith("case")){prevBreak=false;}
        if(str.startsWith("}")){prevBreak=false;}
        if(prevBreak){
            String sym=illegalPrefix+programcounter;
            write("case "+sym+":");
            addSymbol(sym);
        }
        if(str.startsWith("break;")){prevBreak=true;}
        if (file == null) {
            if(initCount>=0){return;}
            System.out.println(str);
        } else {
            try {
                file.append(str + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void addSymbol(String sym)
    {
        if(sym.startsWith(funPrefix))
        {
            functions.add(sym);
            return;
        }
        if(sym.startsWith(tagPrefix))
        {
            tags.add(sym);
            return;
        }
        if(sym.startsWith(retPrefix))
        {
            returns.add(sym);
            return;
        }
        illegals.add(sym);
    }
    public void exec(Command cmd) {
        if (cmd.name.length() == 0) {
            return;
        }
        if (cmd.name.startsWith("$")) {
            fun(cmd.name);
        }
        if (cmd.name.startsWith("@")) {
            tag(cmd.name);
        }
        super.exec(cmd);
    }

    public void fun(String fun) {
        end();
        write("case " + symFormat(fun) + ":");
        write("cpu.mem.push();");
        addSymbol(symFormat(fun));
    }

    public void tag(String tag) {
        write("case " + symFormat(tag) + ":");
        addSymbol(symFormat(tag));
    }

    public void flush() {
        write(footer);
        Collections.sort(tags);
        Collections.sort(functions);
        Collections.sort(returns);
        Collections.sort(illegals);
        for (String sym : tags) {
            write(sym + ",");
        }
        for (String sym : functions) {
            write(sym + ",");
        }
        for (String sym : returns) {
            write(sym + ",");
        }
        for (String sym : illegals) {
            write(sym + ",");
        }
        write("}\n}");
        try {
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INSTRUCTIONS ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void nop() {
        write("cpu.nop();");
    }

    public void var(String v) {
        write("cpu.var(" + varFormat(v) + ");");
    }

    public void gvar(String v) {
        write("cpu.gvar(" + varFormat(v) + ");");
    }

    public void delv(String v) {
        write("cpu.delv(" + varFormat(v) + ");");
    }

    public void delg(String v) {
        write("cpu.delg(" + varFormat(v) + ");");
    }

    public void mov(String res, String v) {
        write("cpu.mov(" + varFormat(res) + "," + varFormat(v) + ");");
    }

    public void add(String res, String v1, String v2) {
        write("cpu.add(" + varFormat(res) + "," + varFormat(v1) + "," + varFormat(v2) + ");");
    }

    public void add() {
        write("cpu.add();");
    }

    public void sub(String res, String v1, String v2) {
        write("cpu.sub(" + varFormat(res) + "," + varFormat(v1) + "," + varFormat(v2) + ");");
    }

    public void sub() {
        write("cpu.sub();");
    }

    public void mul(String res, String v1, String v2) {
        write("cpu.mul(" + varFormat(res) + "," + varFormat(v1) + "," + varFormat(v2) + ");");
    }

    public void mul() {
        write("cpu.mul();");
    }

    public void div(String res, String v1, String v2) {
        write("cpu.div(" + varFormat(res) + "," + varFormat(v1) + "," + varFormat(v2) + ");");
    }

    public void div() {
        write("cpu.div();");
    }

    public void floor(String res, String v) {
        write("cpu.floor(" + varFormat(res) + "," + varFormat(v) + ");");
    }

    public void floor() {
        write("cpu.floor();");
    }

    public void ceil(String res, String v) {
        write("cpu.ceil(" + varFormat(res) + "," + varFormat(v) + ");");
    }

    public void ceil() {
        write("cpu.ceil();");
    }

    public void push(String v) {
        write("cpu.push(" + varFormat(v) + ");");
    }

    public void pop(String res) {
        write("cpu.pop(" + varFormat(res) + ");");
    }

    public void call(String fun) {
        if (functions == null) {
            functions = new ArrayList<>();
        }
        String retSymbol = retPrefix + this.programcounter;
        if(initCount>=0){retSymbol+="_init_"+initCount;}
        if(returns==null){returns=new ArrayList<>();}
        addSymbol(retSymbol);
        write("callStack.push(Symbol." + retSymbol + ");");
        write("sym=Symbol." + symFormat(fun) + ";");
        write("break;");
        write("case " + retSymbol + ":");
    }

    private String symFormat(String sym) {
        sym = sym.replaceAll("\\$", funPrefix);
        sym = sym.replaceAll("\\@", tagPrefix);
        return sym;
    }

    public void ret()
    {
        write("sym=callStack.pop();");
        write("cpu.mem.pop();");
        write("break;");
    }

    public void jmp(String tag) {
        write("sym=Symbol." + symFormat(tag) + ";");
        write("break;");
    }

    public void jz(String v, String tag) {
        write("if(cpu.mem.getValue(" + varFormat(v) + ").isEqual(new Variable(0))){");
        jmp(tag);
        write("}");
    }

    public void jnz(String v, String tag) {
        write("if(!cpu.mem.getValue(" + varFormat(v) + ").isEqual(new Variable(0))){");
        jmp(tag);
        write("}");
    }

    public void jmz(String v, String tag) {
        write("if(cpu.mem.getValue(" + varFormat(v) + ").isBigger(new Variable(0))){");
        jmp(tag);
        write("}");
    }

    public void jlz(String v, String tag) {
        write("if(cpu.mem.getValue(" + varFormat(v) + ").isSmaller(new Variable(0))){");
        jmp(tag);
        write("}");
    }

    public void in(String res, String port) {
        write("cpu.in(" + varFormat(res) + "," + varFormat(port) + ");");
    }

    public void out(String port, String data) {
        write("cpu.out(" + varFormat(port) + "," + varFormat(data) + ");");
    }

    public void setint(String _int, String fun) {
        write("//setint " + varFormat(_int) + " " + symFormat(fun));
    }

    public void _int(String _int) {
        write("//int " + varFormat(_int));
    }

    public void rmint(String _int) {
        write("//rmint " + varFormat(_int));
    }

    public void sleep(String v) {
        write("sleep(" + varFormat(v) + ");");
    }

    public void halt(String v) {
        write("cpu.halt(" + varFormat(v) + ");");
        write("System.out.println(\"press return key to exit or just kill this process\");");
        write("try {System.in.read();}");
        write("catch (IOException e) {e.printStackTrace();}");
        write("System.exit(cpu.mem.getValue("+varFormat(v)+").intValue());");
    }

    public void load(String v, String addr) {
        write("cpu.load(" + varFormat(v) + "," + varFormat(addr) + ");");
    }

    public void store(String addr, String v) {
        write("cpu.store(" +varFormat(addr)+ "," + varFormat(v)+ ");");
    }

    public void trace() {
        if (debug) {
            write("cpu.trace();");
        }
    }

    public void log(String v) {
        if (debug) {
            write("cpu.log(\"" + v + "\");");
        }
    }

    public void print(String[] v) {
        if (debug) {
            String arr = "{";
            for (String s : v) {
                arr += "\"" + s + "\",";
            }
            arr += "}";
            write("cpu.print(" + arr + ");");
        }
    }

    static String header = "" +
            "package com.marasm.mvmJava;\n" +
            "import com.marasm.mvm.main.*;\n" +
            "import com.marasm.ppc.*;\n" +
            "import org.apache.commons.cli.*;\n" +
            "import java.io.IOException;\n" +
            "import java.util.Stack;\n" +
            "public class Main\n" +
            "{\n" +
            "static CPU cpu=new CPU(new Program());\n" +
            "static void init()\n" +
            "{\n" +
            "com.marasm.mvm.main.Main.prepare();\n" +
            "}\n" +
            "public static void main(String[] args) throws Exception\n" +
            "{\n" +
            "Options options=new Options();\n" +
            "options.addOption(\"h\",false,\"print help\");\n" +
            "options.addOption(\"mvmHome\",true,\"set custom mvm home directory\");\n" +
            "CommandLineParser parser = new DefaultParser();\n" +
            "CommandLine cmd = null;\n" +
            "try {\n" +
            "cmd = parser.parse( options, args);\n" +
            "} catch (ParseException e) {\n" +
            "HelpFormatter formatter = new HelpFormatter();\n" +
            "formatter.printHelp(args[0], options);\n" +
            "System.exit(127);\n" +
            "}\n" +
            "if(cmd.hasOption(\"h\"))\n" +
            "{\n" +
            "HelpFormatter formatter = new HelpFormatter();\n" +
            "formatter.printHelp(\"mvm\", options);\n" +
            "System.exit(0);\n" +
            "}\n" +
            "if(cmd.hasOption(\"mvmHome\"))\n" +
            "{\n" +
            "Utils.setMarasmHome(cmd.getOptionValue(\"mvmHome\"));\n" +
            "}" +
            "init();\n" +
            "Symbol sym=Symbol.start;\n" +
            "Stack<Symbol> callStack=new Stack<>();\n" +
            "callStack.push(Symbol.empty);\n" +
            "while (true)\n" +
            "{\n" +
            "switch (sym)\n" +
            "{\n" +
            "case start:";
    static String footer = "" +
            "case  empty:\n" +
            "throw new Exception(\"'ret' without 'call' (empty callstack!)\");\n" +
            "default:\n" +
            "throw new Exception(\"Illegal symbol '\"+sym+\"'\");\n" +
            "}\n" +
            "}\n" +
            "}\n" +
            "enum Symbol\n" +
            "{\n" +
            " empty,start,";
}