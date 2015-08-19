package com.marasm.mvm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sr3u on 19.08.15.
 */
public class Program
{
    ArrayList<Command> program;
    Map<String,Long> functions,tags;
    Map<String,JSONObject> filesLoaded;
    ArrayList<String> initializationFunctions;

    public Program()
    {
        program=new ArrayList<>();
        functions=new HashMap<>();
        tags=new HashMap<>();
        filesLoaded=new HashMap<>();
        initializationFunctions=new ArrayList<>();
    }
    public Program(String path)
    {
        this();
        loadFile(path);
    }
    public String toString()
    {
        String str=new String();
        for(int i=0;i<program.size();i++)
        {
            str+=program.get(i);
            if(i<program.size()-1){str+="\n";}
        }
        return str;
    }
     ArrayList<String> readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return new ArrayList<>(lines);
    }
    public void loadModule(String name)
    {
        if(!name.endsWith(".marasm"))
        {
            name=name+".marasm";
        }
        if(name.startsWith("."))
        {
            name=Utils.workingDir()+name.substring(1);
        }
        else
        {
            name=Utils.marasmModules()+name;
        }
        loadFile(name);
    }
    public void loadFile(String path)
    {
        if(filesLoaded.get(path)!=null){return;}
        ArrayList<String>file;
        try {
            file=readLines(path);
        }
        catch (IOException e)
        {
            Log.error(e.toString()+"\n"+e.getLocalizedMessage()+"\n"+e.getStackTrace().toString());
            return;
        }
        for(int i=0;i<file.size();i++){file.set(i, file.get(i).trim());}
        JSONObject fileInfo=loadFileHeader(file);
        fileInfo.append("fileBegin",program.size());
        if(fileInfo==null){Log.error("In file "+path+"\nFailed to load header from file "+path);}
        try{initializationFunctions.add(fileInfo.getString("init"));}catch (JSONException e){}
        try{
            JSONArray deps=fileInfo.getJSONArray("dependencies");
            for(int i=0;i<deps.length();i++)
            {
                loadModule(deps.getString(i));
            }
        }catch (JSONException e){}
        //TODO check devices
        ArrayList<Command>cmds=new ArrayList<>();
        for (int i=0;i<file.size();i++)
        {
            Command tmp=new Command(file.get(i));
            if(tmp.name.startsWith("$")){
                if(functions.get(tmp.name)!=null){Log.error("In file "+path+"\ndublicated function '"+tmp.name+"'");}
                functions.put(tmp.name,new Long(i+program.size()));
            }else if(tmp.name.startsWith("@")) {
                if(tags.get(tmp.name)!=null){Log.error("In file "+path+"\ndublicated tag '"+tmp.name+"'");}
                tags.put(tmp.name, new Long(i + program.size()));
            }
            cmds.add(tmp);
        }
        program.addAll(cmds);
        fileInfo.append("fileEnd",program.size());
        filesLoaded.put(path,fileInfo);
    }
    public JSONObject loadFileHeader(ArrayList<String>file)
    {
        int i;
        for(i=0;i<file.size();i++){if(file.get(i).startsWith("#json")){break;}}
        if(i>file.size()){return null;}
        long headerBegin,headerEnd;
        headerEnd=headerBegin=i;
        ArrayList<String>headerStr=new ArrayList<>();
        file.remove(i);
        while(file.size()>0)
        {
            String tmp=file.get(i);
            file.remove(i);
            headerEnd++;
            if(tmp.startsWith("#end")){break;}
            headerStr.add(tmp);
        }
        try {
            String jsonStr=new String();
            for(int j=0;j<headerStr.size();j++)
            {
                jsonStr+=headerStr.get(j);
            }
            JSONObject header=new JSONObject(jsonStr);
            header.append("headerBegin",headerBegin);
            header.append("headerEnd",headerEnd);
            return header;
        }
        catch(JSONException e)
        {
            return null;
        }
    }
    public Command getCommand(long pc)
    {
        if(pc>program.size()){Log.error("program counter "+pc+" is out of bounds!");return null;}
        return program.get((int) pc);
    }
    public long getFun(String fun)
    {
        Long f=functions.get(fun);
        if(f==null)
        {
            Log.error("function '"+fun+"' is not loaded");
            return 0;
        }
        return f.longValue();
    }
    public long getTag(String tag)
    {
        Long t=tags.get(tag);
        if(t==null)
        {
            Log.error("function '"+tag+"' is not loaded");
            return 0;
        }
        return t.longValue();
    }
    public long size()
    {
        return program.size();
    }
}
