package com.marasm.mvm.main;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by sr3u on 19.08.15.
 */
public class Utils
{
    private static String mvm_home;
    public static void setMarasmHome(String newHome)
    {
        File f=new File(newHome);
        mvm_home=f.getAbsolutePath();
    }
    public static String mainJarLocation()
    {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsoluteFile().getParent();
    }
    public static String jarLocation(Class c)
    {
        return new File(c.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsoluteFile().getParent();
    }
    public static String homeDir(){return System.getProperty("user.home")+File.separator;}
    public static String marasmHome()
    {
        if(mvm_home==null){return mainJarLocation();}
        return mvm_home;
    }
    public static String marasmModules()
    {
        String home=marasmHome();
        String tmp=home.substring(home.length()-1);
        if(!tmp.equals(File.separator))
            home+=File.separator;
        return home+"modules"+File.separator;
    }
    public static String marasmDevices()
    {
        String home=marasmHome();
        String tmp=home.substring(home.length() - 1);
        if(!tmp.equals(File.separator))
            home+=File.separator;
        return home+"devices"+File.separator;
    }
    public static String workingDir()
    {
        return Paths.get("").toAbsolutePath().toString();
    }
    public static String unescape(String str)
    {
        str = str.replaceAll("\\\\n", "\n");
        str = str.replaceAll("\\\\r", "\r");
        str = str.replaceAll("\\\\\"", "\"");
        str = str.replaceAll("\\\\'", "\'");
        str = str.replaceAll("\\\\\\\\", "\\");
        return str;
    }
}
