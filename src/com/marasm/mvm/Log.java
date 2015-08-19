package com.marasm.mvm;

/**
 * Created by sr3u on 19.08.15.
 */
public class Log
{
    private static ErrorHandler eh;
    public static void setErrorHandler(ErrorHandler _eh){eh=_eh;}
    public static void info(Object o){System.out.println("INFO: " + o.toString());}
    public static void error(Object o){
        System.out.println("ERROR: " + o.toString());
        if(eh!=null){eh.error();}
    }
    public static void warning(Object o){
        System.out.println("WARNING: " + o.toString());
        if(eh!=null){eh.warning();}
    }
    public static void trace(Object o){System.out.println("TRACE:\n" + o.toString());}
}
