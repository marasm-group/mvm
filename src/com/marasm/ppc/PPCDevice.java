package com.marasm.ppc;

/**
 * Created by sr3u on 19.08.15.
 */
public class PPCDevice
{
    public void connected(){}
    public void out(Variable port,Variable data){}
    public Variable in(Variable port){return new Variable();}
    public String manufacturer(){return getClass().getPackage().getImplementationVendor();}
    String ctrlBuf=new String();
    public void ctrlOut(Variable data)
    {
        if(data.equals(CTRL.NOP)){return;}
        if(data.equals(CTRL.GETMAN)){
            ctrlBuf=manufacturer();
            return;}
    }
    public Variable ctrlIn()
    {
        if(ctrlBuf.length()>0)
        {
            if(ctrlBuf.length()==0) {ctrlBuf=new String();}
            if(ctrlBuf.length()<2){
                Variable tmp=new Variable(ctrlBuf.substring(0,1).getBytes()[0]);
                ctrlBuf=ctrlBuf.substring(1);
                return tmp;
            }
            else{
                Variable v=new Variable(ctrlBuf.substring(0,1).getBytes()[0]);
                ctrlBuf=ctrlBuf.substring(1);
                return v;
            }
        }
        return new Variable();
    }
}
