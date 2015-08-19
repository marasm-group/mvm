package com.marasm.mvm;

import java.nio.file.Paths;

/**
 * Created by sr3u on 19.08.15.
 */
public class Utils
{
    public static String mainJarLocation()
    {
        return Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
    public static String jarLocation(Class c)
    {
        return c.getProtectionDomain().getCodeSource().getLocation().getPath();
    }
    public static String homeDir(){return System.getProperty("user.home")+"/";}
    public static String marasmHome()
    {
        return mainJarLocation();
    }
    public static String marasmModules()
    {
        return marasmHome()+"modules/";
    }
    public static String marasmDevices()
    {
        return marasmHome()+"devices/";
    }
    public static String workingDir()
    {
        return Paths.get("").toAbsolutePath().toString();
    }
}
