package com.marasm.ppc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sr3u on 01.09.15.
 */
public class RAM
{
    private Map<String,Variable> ram= new HashMap<>();
    private static RAM instance=new RAM();
    private RAM_Device device = new RAM_Device();
    private RAM()
    {
        device.connected();
    }
    public static RAM getInstance(){return instance;}
    public String toString()
    {
        String res=new String();
        res+="RAM:\n";
        for(Map.Entry<String, Variable> entry : ram.entrySet())
        {res+="\t"+entry.getKey().toString()+" : "+entry.getValue().toString()+"\n";}
        res+="\n";
        return res;
    }
    private void allocate(Variable addr)
    {
        ram.put(addr.toString(),new Variable());
    }
    public Variable _load (Variable addr)
    {
        Variable res;
        res=ram.get(addr.toString());
        if(res==null){
            res=new Variable();
            allocate(addr);
        }
        else{res=new Variable(res);}
        return res;
    }
    public void _store (Variable addr,Variable value)
    {
       ram.put(addr.toString(),new Variable(value));
    }
    public static Variable load (Variable addr){return instance._load(addr);}
    public static void store (Variable addr,Variable value){instance._store(addr,value);}
    public static String string()
    {
        return instance.toString();
    }

    static class RAM_Device extends  PPCDevice
    {
        final String CTRL_Port = "1";
        final String AllocInfo_Port = "1.1";
        final String Alloc_Port = "1.2";
        final String Free_Port = "1.3";

        Map<Long,Long> allocated = new HashMap<>();
        boolean allocInfo = false;
        boolean freeStatus = false;

        long allocCell= -1;

        public void connected()
        {
            PPC.connect(new Variable(CTRL_Port),this);
            PPC.connect(new Variable(AllocInfo_Port),this);
            PPC.connect(new Variable(Alloc_Port),this);
            PPC.connect(new Variable(Free_Port),this);
        }
        public void out(Variable port,Variable data)
        {
            switch (port.toString())
            {
                case CTRL_Port:
                    this.ctrlOut(data);
                    break;
                case AllocInfo_Port:
                    allocInfo = allocated(data.longValue());
                    break;
                case Alloc_Port:
                    this.allocate(data.longValue());
                    break;
                case Free_Port:
                    this.free(data.longValue());
                    break;
            }
        }
        public Variable in(Variable port)
        {
            switch (port.toString())
            {
                case CTRL_Port:
                    return ctrlIn();
                case AllocInfo_Port:
                    return new Variable(allocInfo);
                case Alloc_Port:
                    return new Variable(allocCell);
                case Free_Port:
                    return new Variable(freeStatus);
            }
            return new Variable(0);
        }


        boolean allocated(long cell)
        {
            if (allocated.containsKey(cell)) {return true;}
            return false;
        }
        void allocate(long size)
        {
            allocCell = -1;
            for (long i = 0; ;i++)
            {
                if(!allocated(i))
                {
                    boolean found = true;
                    for(long j = i;j<i+size;j++)
                    {
                        if(allocated(j))
                        {
                            found = false;
                            break;
                        }
                    }
                    if(found)
                    {
                        allocCell = i;
                        for(long k = i; k<i+size;k++)
                        {
                            allocated.put(k,size);
                        }
                        Log.info("RAM: allocated "+size+" cells at "+allocCell+" total allocated: "+allocated.size());
                        return;
                    }
                    else
                    {
                        Log.error("RAM: failed to allocate "+size+" cells total allocated: "+allocated.size());
                    }
                }
            }
        }
        void free(long cell)
        {
            if(!allocated(cell)){return;}
            long size = allocated.get(cell);
            for(long i = cell;i<cell+size;i++)
            {
                allocated.remove(i);
            }
            Log.info("RAM: freed "+size+" cells at "+cell+" total allocated: "+allocated.size());
        }
    }
}
