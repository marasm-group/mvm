package com.marasm.mvm;

/**
 * Created by sr3u on 19.08.15.
 */
public class Command
{
    public String name;
    public String[] args;
    public String comment;

    public Command(String str)
    {
        int idx = str.indexOf(";");
        if (idx!= -1)
        {
            comment=str.substring(idx);
            str = str.substring(0, idx);
        }
        else{comment=new String();}
        String[] arr=str.split(" ");
        name=arr[0];
        args=new String[arr.length-1];
        for(int i=1;i<arr.length;i++)
        {
            args[i-1]=arr[i];
        }
    }
    public String toString()
    {
        String argstr=new String();
        for(int i=0;i<args.length;i++)
            argstr+=args[i]+" ";
        argstr.trim();
        String res=name+" "+argstr+" "+comment;
        res=res.trim();
        return res;
    }
}
