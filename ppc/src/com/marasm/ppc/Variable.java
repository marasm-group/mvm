package com.marasm.ppc;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by sr3u on 19.08.15.
 */
public class Variable
{
    BigDecimal value;
    public Variable(){this(new BigDecimal(0));}
    public Variable(BigDecimal val){value=val;}
    public Variable(Variable var){this(var.value);}
    public Variable(String str)
    {
        BigDecimal v;
        try{v=new BigDecimal(str);}
        catch (NumberFormatException e){value=new BigDecimal(0);return;}
        value=v;
    }

    public Variable(int i) {this(new BigDecimal(i));}
    public Variable(long i) {this(new BigDecimal(i));}
    public Variable(float i) {this(new BigDecimal(i));}
    public Variable(double i) {this(new BigDecimal(i));}
    public String toString()
    {
        return value.toString();
    }
    public int intValue(){return value.intValue();}
    public long longValue(){return value.longValue();}
    public float floatValue(){return value.floatValue();}
    public double doubleValue(){return value.doubleValue();}
    public void set(Variable v)
    {
        value=v.value;
    }
    public Variable add(Variable v)
    {
        try{return  new Variable(value.add(v.value));}
        catch (ArithmeticException e){
            InterruptsController.Interrupt(InterruptsController.int_Arithmetic);
            return new Variable();
        }
    }
    public Variable sub(Variable v)
    {
        try{return  new Variable(value.subtract(v.value));}
        catch (ArithmeticException e){
            InterruptsController.Interrupt(InterruptsController.int_Arithmetic);
            return new Variable();
        }
    }
    public Variable mul(Variable v)
    {
        try{return  new Variable(value.multiply(v.value));}
        catch (ArithmeticException e){
            InterruptsController.Interrupt(InterruptsController.int_Arithmetic);
            return new Variable();
        }
    }
    public Variable div(Variable v)
    {
        try{return  new Variable(value.divide(v.value,32,RoundingMode.HALF_UP));}
        catch (ArithmeticException e){
            InterruptsController.Interrupt(InterruptsController.int_Arithmetic);
            return new Variable();
        }
    }
    public Variable floor()
    {
        return new Variable(value.setScale(0, BigDecimal.ROUND_FLOOR));
    }
    public Variable ceil()
    {
        return new Variable(value.setScale(0,BigDecimal.ROUND_CEILING));
    }
    public boolean isEqual(Variable v) {return  value.compareTo(v.value)==0;}
    public boolean isBigger(Variable v){return  value.compareTo(v.value)>0;}
    public boolean isSmaller(Variable v) {return value.compareTo(v.value) < 0;}
    public static Variable Character(String chr)
    {
        if(chr.length()==0){return new Variable();}
        return new Variable(chr.getBytes()[1]);
    }
}
