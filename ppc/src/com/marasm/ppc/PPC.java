package com.marasm.ppc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by sr3u on 19.08.15.
 */
public class PPC
{
    static Map<String,PPCDevice> devices=new HashMap<>();

    static public void connect(Variable port,PPCDevice dev)
    {
        devices.put(port.toString(), dev);
    }
    static public void disconnect(Variable port)
    {
        devices.remove(port.toString());
    }
    public static void out(Variable port,Variable data)
    {
        PPCDevice dev=devices.get(port.toString());
        if(dev!=null){dev.out(port, data);return;}
        Log.warning("No device on port '" + port + "' data " + data);
    }
    public static Variable in(Variable port)
    {
        PPCDevice dev=devices.get(port.toString());
        if(dev==null){
            Log.warning("No device on port '" + port + "'");
            return new Variable();
        }
        Variable v=dev.in(port);
        if(v==null){return new Variable();}
        return v;
    }
    public static void LoadDevices(String folder)
    {
        String[]devs=PPCUtils.listFiles(folder,".jar");
        if(devs==null){return;}
        for (String dev:devs)
        {
            LoadDevice(dev);
        }
    }
    public static void LoadDevice(String path)
    {
        try {
            File file = new File(path);
            JarFile jar= null;
            jar = new JarFile(path);
            Manifest man=jar.getManifest();

            Attributes attr=man.getMainAttributes();
            String mainClass=attr.getValue("Main-Class");
            URL jarfile = new URL("jar", "","file:" + file.getAbsolutePath()+"!/");
            URLClassLoader cl = URLClassLoader.newInstance(new URL[]{jarfile});
            Class<? extends PPCDevice>loadedClass = cl.loadClass(mainClass).asSubclass(PPCDevice.class);
            loadedClass.newInstance().connected();
            Log.info("connected device: "+file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
