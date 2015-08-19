package com.marasm.mvm.ppc;

import java.math.BigDecimal;

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
    public Variable add(Variable v)
    {
        return  new Variable(value.add(v.value));
    }
    public Variable sub(Variable v)
    {
        return  new Variable(value.subtract(v.value));
    }
    public Variable mul(Variable v)
    {
        return  new Variable(value.multiply(v.value));
    }
    public Variable div(Variable v)
    {
        return  new Variable(value.divide(v.value));
    }
    public Variable floor()
    {
        return new Variable(value.setScale(0,BigDecimal.ROUND_FLOOR));
    }
    public Variable ceil()
    {
        return new Variable(value.setScale(0,BigDecimal.ROUND_CEILING));
    }
}
