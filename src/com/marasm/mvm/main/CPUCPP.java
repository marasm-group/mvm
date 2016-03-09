package com.marasm.mvm.main;

import com.marasm.ppc.*;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by vhq473 on 09.03.2016.
 */
public class CPUCPP extends CPU
{
    FileWriter file;
    String globalVariablesStr=cppHeader+"";
    ArrayList<String> variables=new ArrayList<>();
    ArrayList<String> gVariables=new ArrayList<>();
    String code="int main()\n{\n";
    String funCode="";
    String variablesStr="";
    String tagPrefix="__at_";
    String funPrefix="__fun_";
    String varDot="_dot_";
    boolean mainEnded=false;
    public CPUCPP(Program p,String outputFile)
    {
        super(p);
        for(String fun : program.initializationFunctions) {
            call(fun);
        }
        try {
            file=new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    String varFormat(String str)
    {
        try
        {
            BigDecimal bd=new BigDecimal(str);
            return "Variable(\""+str+"\")";
        }
        catch (NumberFormatException e)
        {
            str=str.replaceAll("\\.",varDot);
            str=str.replaceAll("\\[","[toInteger(");
            str=str.replaceAll("\\]",")]");
            return str;
        }
    }
    String varFormatVar(String str)
    {
        str=varFormat(str);
        if(str.contains("[toInteger("))
        {
            str="*"+str;
            str=str.replaceFirst("\\[toInteger\\(","=new Variable[toInteger(");
        }
        return str;
    }
    void end()
    {
        mainEnded=true;
        variables.clear();
        code+=variablesStr+"\n";
        code+=funCode;
        funCode="";
        variablesStr="";
        if(!mainEnded){code+="\nreturn 0\n}\n";}
        else{code+="\n}\n";}
    }
    public void write(String str)
    {
        funCode+=str+"\n";
    }
    public void fwrite(String str)
    {
        if(file==null){System.out.println(str);}
        else{
            try {
                file.append(str+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void exec(Command cmd)
    {
        if(cmd.name.length()==0){return;}
        if(cmd.name.startsWith("$")){
            fun(cmd.name);
        }
        if(cmd.name.startsWith("@")){tag(cmd.name);}
        super.exec(cmd);
    }
    void fun(String fun)
    {
        end();
        String funstr="void "+funPrefix+fun.replace("$","")+"()";
        globalVariablesStr+=funstr+";\n";
        code+=funstr+"\n{\n";
    }
    void tag(String tag)
    {
        write(tag.replace("@",tagPrefix)+":\n");
    }
    void flush()
    {
        fwrite(globalVariablesStr);
        fwrite(code);
        try {
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INSTRUCTIONS ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void nop(){write("nop()");}
    void var(String v)
    {
        if(variables.contains(v)){return;}
        variables.add(v);
        variablesStr +="Variable "+varFormatVar(v)+";\n";
    }
    void gvar(String v)
    {
        if(gVariables.contains(v)){return;}
        gVariables.add(v);
        globalVariablesStr +="Variable "+varFormatVar(v)+";\n";
    }
    void delv(String v)
    {
        write(v+"="+"Variable::ZERO;");
    }
    void delg(String v)
    {
        write(v+"="+"Variable::ZERO;");
    }
    void mov(String res,String v)
    {
        write("mov("+varFormat(res)+","+varFormat(v)+");");
    }
    void add(String res,String v1,String v2)
    {
        write("add("+varFormat(res)+","+varFormat(v1)+","+varFormat(v2)+");");
    }
    void add()
    {
        write("adds();");
    }
    void sub(String res,String v1,String v2)
    {
        write("sub("+varFormat(res)+","+varFormat(v1)+","+varFormat(v2)+");");
    }
    void sub()
    {
        write("subs();");
    }
    void mul(String res,String v1,String v2)
    {
        write("mul("+varFormat(res)+","+varFormat(v1)+","+varFormat(v2)+");");
    }
    void mul()
    {
        write("muls();");
    }
    void div(String res,String v1,String v2)
    {
        write("div("+varFormat(res)+","+varFormat(v1)+","+varFormat(v2)+");");
    }
    void div()
    {
        write("divs();");
    }
    void floor(String res,String v)
    {
        write("floor("+varFormat(res)+","+varFormat(v)+");");
    }
    void floor(){write("floors();");}
    void ceil(String res,String v)
    {
        write("ceil("+varFormat(res)+","+varFormat(v)+");");
    }
    void ceil(){write("ceils();");}
    void push(String v)
    {
        v.replaceAll(" ","");
        if(v.startsWith("'"))
        {
            v=mem.getValue(v).toString();
        }
        v=varFormat(v);
        write("push("+v+");");
    }
    void pop(String res)
    {
        write("pop("+varFormat(res)+");");
    }
    void call(String fun)
    {
        write(funPrefix+fun.replace("$","")+"();");
    }
    void ret()
    {
        write("return;");
    }
    void jmp(String tag)
    {
        write("jmp("+tag.replace("@",tagPrefix)+");");
    }
    void jz(String v,String tag){write("jz("+varFormat(v)+","+tag.replace("@",tagPrefix)+");");}
    void jnz(String v,String tag){write("jnz("+varFormat(v)+","+tag.replace("@",tagPrefix)+");");}
    void jmz(String v,String tag){write("jmz("+varFormat(v)+","+tag.replace("@",tagPrefix)+");");}
    void jlz(String v,String tag){write("jlz("+varFormat(v)+","+tag.replace("@",tagPrefix)+");");}
    void in(String res,String port){write("in_("+varFormat(res)+","+varFormat(port)+");");}
    void out(String port,String data){write("out_("+varFormat(port)+","+varFormat(data)+");");}
    void setint(String _int,String fun){write("//setint "+varFormat(_int)+" "+fun);}
    void _int(String _int){write("//int "+varFormat(_int));}
    void rmint(String _int){write("//rmint "+varFormat(_int));}
    void sleep(String v)
    {
       write("sleep("+varFormat(v)+");");
    }
    void halt(String v)
    {
        write("halt("+varFormat(v)+");");
    }
    void load(String v,String addr)
    {
        write("load("+varFormat(v)+","+varFormat(addr)+");");
    }
    void store(String addr,String v)
    {
        write("store("+varFormat(addr)+","+varFormat(v)+");");
    }
    void trace(){write("trace();");}
    void log(String v)
    {
        write("std::cout<<\""+v+"\"<<'\n';");
    }
    void print(String[] v)
    {
        if(debug)
        {
            for(String str : v)
            {
                write("std::cout<<\""+str+"<<\"=<<"+str+"'\n';");
            }
        }
    }
    static String cppHeader="" +
            "#ifndef MVMCPP\n" +
            "#define MVMCPP\n" +
            "#include <iostream>\n" +
            "#include <stack>\n" +
            "#include <string>\n" +
            "#include <sstream>\n" +
            "#include <vector>\n" +
            "#include <math.h>\n" +
            "using namespace std;\n" +
            "typedef char* CString;\n" +
            "typedef string AutoString;\n" +
            "char exSeparator='.';\n" +
            "class Decimal\n" +
            "{\n" +
            "public:\n" +
            "    \n" +
            "    static void Initialize(int precision=10);\n" +
            "    Decimal(void);\n" +
            "    Decimal(AutoString num);\n" +
            "    Decimal(const Decimal& d);\n" +
            "    Decimal(const int64_t n);\n" +
            "    Decimal(int intPart, int fractPart);\n" +
            "    \n" +
            "    virtual ~Decimal(void);\n" +
            "    \n" +
            "    Decimal operator+(const Decimal&)const;\n" +
            "    Decimal operator-(const Decimal&)const;\n" +
            "    Decimal operator*(const Decimal&)const;\n" +
            "    Decimal operator/(const Decimal&)const;\n" +
            "    \n" +
            "    Decimal operator +=(const Decimal&);\n" +
            "    Decimal operator -=(const Decimal&);\n" +
            "    Decimal operator *=(const Decimal&);\n" +
            "    Decimal operator /=(const Decimal&);\n" +
            "    \n" +
            "    Decimal floor()const;\n" +
            "    Decimal ceiling()const;\n" +
            "    \n" +
            "    bool operator==(const Decimal&) const;\n" +
            "    bool operator!=(const Decimal&) const;\n" +
            "    bool operator<(const Decimal&) const;\n" +
            "    bool operator<=(const Decimal&) const;\n" +
            "    bool operator>(const Decimal&) const;\n" +
            "    bool operator>=(const Decimal&) const;\n" +
            "    \n" +
            "    AutoString toString(void) const;\n" +
            "    double toDouble(void) const;\n" +
            "    \n" +
            "    static Decimal ZERO;\n" +
            "    static Decimal ONE;\n" +
            "    int64_t toInteger(void)const;\n" +
            "protected:\n" +
            "    \n" +
            "    int64_t n;\n" +
            "    \n" +
            "    static int precision;\n" +
            "    static int64_t q;\n" +
            "    static char* pad;\n" +
            "};\n" +
            "\n" +
            "int Decimal::precision=0;\n" +
            "int64_t Decimal::q=0;\n" +
            "char* Decimal::pad=0L;\n" +
            "\n" +
            "Decimal Decimal::ZERO=Decimal(0,0);\n" +
            "Decimal Decimal::ONE=Decimal(1,0);\n" +
            "\n" +
            "// static initialization.  Sets up all decimal arithmetic functions\n" +
            "void Decimal::Initialize(int prec)\n" +
            "{\n" +
            "    precision=prec;\n" +
            "    // create an array of 0's for padding\n" +
            "    pad=new char[precision+1];\n" +
            "    memset(pad, '0', precision);\n" +
            "    pad[precision]='\\0';\n" +
            "    // get fractional precision\n" +
            "    q=(int64_t)pow(10.0, (double)prec);\n" +
            "}\n" +
            "\n" +
            "Decimal::Decimal(void)\n" +
            "{\n" +
            "    if(pad==NULL){Initialize();}\n" +
            "    n=0;\n" +
            "}\n" +
            "\n" +
            "Decimal::Decimal(const Decimal& d)\n" +
            "{\n" +
            "    if(pad==NULL){Initialize();}\n" +
            "    n=d.n;\n" +
            "}\n" +
            "\n" +
            "Decimal::Decimal(const int64_t n)\n" +
            "{\n" +
            "    if(pad==NULL){Initialize();}\n" +
            "    this->n=n;\n" +
            "}\n" +
            "\n" +
            "Decimal::Decimal(int intPart, int fractPart)\n" +
            "{\n" +
            "    if(pad==NULL){Initialize();}\n" +
            "    n=intPart;\n" +
            "    n*=q;\n" +
            "    n+=(int64_t)fractPart;\n" +
            "}\n" +
            "AutoString leftEx(AutoString str,char c);\n" +
            "AutoString rightEx(AutoString str,char c);\n" +
            "Decimal::Decimal(AutoString num)\n" +
            "{\n" +
            "    if(pad==NULL){Initialize();}\n" +
            "    // get the integer component\n" +
            "    AutoString intPart=leftEx(num,exSeparator);\n" +
            "    // get the fractional component\n" +
            "    AutoString fractPart=rightEx(num,exSeparator);\n" +
            "    \n" +
            "    // \"multiply\" the fractional part by the desired precision\n" +
            "    fractPart+=&pad[fractPart.length()];\n" +
            "    \n" +
            "    // create the 64bit integer as a composite of the\n" +
            "    // integer and fractional components.\n" +
            "    n=atoi(intPart.c_str());\n" +
            "    n*=q;\n" +
            "    n+=atoi(fractPart.c_str());\n" +
            "}\n" +
            "\n" +
            "Decimal::~Decimal(void)\n" +
            "{\n" +
            "}\n" +
            "\n" +
            "Decimal Decimal::operator+(const Decimal& d)const\n" +
            "{\n" +
            "    return Decimal(n + d.n);\n" +
            "}\n" +
            "\n" +
            "Decimal Decimal::operator-(const Decimal& d)const\n" +
            "{\n" +
            "    return Decimal(n - d.n);\n" +
            "}\n" +
            "\n" +
            "Decimal Decimal::operator*(const Decimal& d)const\n" +
            "{\n" +
            "    return Decimal(n * d.n / q);\n" +
            "}\n" +
            "\n" +
            "Decimal Decimal::operator/(const Decimal& d)const\n" +
            "{\n" +
            "    return Decimal(n * q / d.n);\n" +
            "}\n" +
            "\n" +
            "\n" +
            "Decimal Decimal::operator +=(const Decimal& d)\n" +
            "{\n" +
            "    n=n + d.n;\n" +
            "    return *this;\n" +
            "}\n" +
            "\n" +
            "Decimal Decimal::operator -=(const Decimal& d)\n" +
            "{\n" +
            "    n=n - d.n;\n" +
            "    return *this;\n" +
            "}\n" +
            "\n" +
            "Decimal Decimal::operator *=(const Decimal& d)\n" +
            "{\n" +
            "    n=n * d.n / q;\n" +
            "    return *this;\n" +
            "}\n" +
            "\n" +
            "Decimal Decimal::operator /=(const Decimal& d)\n" +
            "{\n" +
            "    n=n * q / d.n;\n" +
            "    return *this;\n" +
            "}\n" +
            "\n" +
            "bool Decimal::operator==(const Decimal& d) const\n" +
            "{\n" +
            "    return n == d.n;\n" +
            "}\n" +
            "\n" +
            "bool Decimal::operator!=(const Decimal& d) const\n" +
            "{\n" +
            "    return n != d.n;\n" +
            "}\n" +
            "\n" +
            "bool Decimal::operator<(const Decimal& d) const\n" +
            "{\n" +
            "    return n < d.n;\n" +
            "}\n" +
            "\n" +
            "bool Decimal::operator<=(const Decimal& d) const\n" +
            "{\n" +
            "    return n <= d.n;\n" +
            "}\n" +
            "\n" +
            "bool Decimal::operator>(const Decimal& d) const\n" +
            "{\n" +
            "    return n > d.n;\n" +
            "}\n" +
            "\n" +
            "bool Decimal::operator>=(const Decimal& d) const\n" +
            "{\n" +
            "    return n >= d.n;\n" +
            "}\n" +
            "\n" +
            "AutoString Decimal::toString(void) const\n" +
            "{\n" +
            "    std::stringstream buf;\n" +
            "    int64_t n2=n/q;\n" +
            "    int64_t fract=(int64_t)(n-n2*q);\n" +
            "    buf<<(int64_t)n2;\n" +
            "    if(fract>0)\n" +
            "    {\n" +
            "        buf<<exSeparator;\n" +
            "        while(1)\n" +
            "        {\n" +
            "            fract*=10;\n" +
            "            if(fract>=q){break;}\n" +
            "            buf<<'0';\n" +
            "        }\n" +
            "        buf<<fract;\n" +
            "    }\n" +
            "    AutoString str=buf.str();\n" +
            "    str.erase ( str.find_last_not_of('0') + 1, std::string::npos );\n" +
            "    if(str.length()==0){str=\"0\";}\n" +
            "    return str;\n" +
            "}\n"+
            "double Decimal::toDouble(void) const\n" +
            "{\n" +
            "    return atof(toString().c_str());\n" +
            "}\n" +
            "vector<string> &split(const string &s, char delim, vector<string> &elems)\n" +
            "{\n" +
            "    stringstream ss(s);\n" +
            "    string item;\n" +
            "    while (getline(ss, item, delim)) {\n" +
            "        elems.push_back(item);\n" +
            "    }\n" +
            "    return elems;\n" +
            "}\n" +
            "vector<string> split(const string &s, char delim)\n" +
            "{\n" +
            "    vector<string> elems;\n" +
            "    split(s, delim, elems);\n" +
            "    return elems;\n" +
            "}\n" +
            "AutoString leftEx(AutoString str,char c){return split(str,c)[0];}\n" +
            "AutoString rightEx(AutoString str,char c)\n" +
            "{\n" +
            "    vector<AutoString>res=split(str,c);\n" +
            "    if(res.size()<2){return \"\";}\n" +
            "    return res[1];\n" +
            "}\n" +
            "Decimal Decimal::floor()const{return Decimal(leftEx(toString(),exSeparator));}\n" +
            "Decimal Decimal::ceiling()const\n" +
            "{\n" +
            "    AutoString str=toString();\n" +
            "    if(leftEx(str,exSeparator).length()<=0){return floor();}\n" +
            "    return floor()+ONE;\n" +
            "}\n" +
            "int64_t Decimal::toInteger(void)const\n" +
            "{\n" +
            "    return this->n/q;\n" +
            "}\n" +
            "\n" +
            "typedef Decimal Variable;\n" +
            "std::stack<Variable>STACK;\n" +
            "\n" +
            "#define nop() ;\n" +
            "void push(const Variable&v){STACK.push(v);}\n" +
            "Variable pop()\n" +
            "{\n" +
            "    Variable res=STACK.top();\n" +
            "    STACK.pop();\n" +
            "    return res;\n" +
            "}\n" +
            "void pop(Variable &v){v=pop();}\n" +
            "void mov(Variable&r,const Variable&a){r=a;}\n" +
            "void add(Variable&r,const Variable&a,const Variable&b){r=a+b;}\n" +
            "void adds(void)\n" +
            "{\n" +
            "    Variable r;\n" +
            "    add(r,pop(),pop());\n" +
            "    push(r);\n" +
            "}\n" +
            "void sub(Variable&r,const Variable&a,const Variable&b){r=a-b;}\n" +
            "void subs(void)\n" +
            "{\n" +
            "    Variable r;\n" +
            "    sub(r,pop(),pop());\n" +
            "    push(r);\n" +
            "}\n" +
            "void mul(Variable&r,const Variable&a,const Variable&b){r=a*b;}\n" +
            "void muls(void)\n" +
            "{\n" +
            "    Variable r;\n" +
            "    mul(r,pop(),pop());\n" +
            "    push(r);\n" +
            "}\n" +
            "void div(Variable&r,const Variable&a,const Variable&b){r=a+b;}\n" +
            "void divs(void)\n" +
            "{\n" +
            "    Variable r;\n" +
            "    div(r,pop(),pop());\n" +
            "    push(r);\n" +
            "}\n" +
            "void floor(Variable&r,const Variable&a){r=a.floor();}\n" +
            "void floors(void)\n" +
            "{\n" +
            "    Variable r;\n" +
            "    floor(r,pop());\n" +
            "    push(r);\n" +
            "}\n" +
            "void ceil(Variable&r,const Variable&a){r=a.ceiling();}\n" +
            "void ceils(void)\n" +
            "{\n" +
            "    Variable r;\n" +
            "    ceil(r,pop());\n" +
            "    push(r);\n" +
            "}\n" +
            "#define jmp(tag) goto tag\n" +
            "#define jz(v,tag) if(v==Variable::ZERO){goto tag;}\n" +
            "#define jnz(v,tag) if(v!=Variable::ZERO){goto tag;}\n" +
            "#define jmz(v,tag) if(v>Variable::ZERO){goto tag;}\n" +
            "#define jlz(v,tag) if(v<Variable::ZERO){goto tag;}\n" +
            "void in_(Variable&r,const Variable&port)\n" +
            "{\n" +
            "    std::cout<<\"in not implemented\\n\";\n" +
            "    r=Variable::ZERO;\n" +
            "}\n" +
            "void out_(const Variable&port,const Variable&d)\n" +
            "{\n" +
            "    std::cout<<\"out not implemented \";\n" +
            "    std::cout<<d.toString()<<\"->\"<<port.toString()<<'\\n';\n" +
            "}\n" +
            "void sleep(const Variable&v){usleep((int64_t)v.floor().toDouble());}\n" +
            "int64_t toInteger(const Variable&v){return v.toInteger();}\n" +
            "int64_t toInteger(const int64_t&v){return v;}\n" +
            "void halt(const Variable&v)\n" +
            "{\n" +
            "    std::cout<<\"halt with code: \"<<v.toString()<<\"\\n\";\n" +
            "    exit(toInteger(v));\n" +
            "}\n" +
            "void trace(){std::cout<<\"trace is not available!\\n\";}\n" +
            "#endif /* MVMCPP */\n";
}
