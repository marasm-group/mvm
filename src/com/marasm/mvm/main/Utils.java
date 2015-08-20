package com.marasm.mvm.main;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by sr3u on 19.08.15.
 */
public class Utils
{
    public static String mainJarLocation()
    {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsoluteFile().getParent();
    }
    public static String jarLocation(Class c)
    {
        return new File(c.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsoluteFile().getParent();
    }
    public static String homeDir(){return System.getProperty("user.home")+"/";}
    public static String marasmHome()
    {
        return mainJarLocation();
    }
    public static String marasmModules()
    {
        String home=marasmHome();
        String tmp=home.substring(home.length()-1);
        if(!tmp.equals("/"))
            home+="/";
        return home+"modules/";
    }
    public static String marasmDevices()
    {
        String home=marasmHome();
        String tmp=home.substring(home.length() - 1);
        if(!tmp.equals("/"))
            home+="/";
        return home+"devices/";
    }
    public static String workingDir()
    {
        return Paths.get("").toAbsolutePath().toString();
    }
    public static String unescape(String str)
    {
        str=str.replaceAll("\\\\","\\");
        str=str.replaceAll("\\n","\n");
        str=str.replaceAll("\\r","\r");
        str=str.replaceAll("\\\"","\"");
        str=str.replaceAll("\\\'","\'");
        return str;
    }
}
