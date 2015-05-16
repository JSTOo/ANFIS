package memberships;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Nixy on 07.03.2015.
 */

public abstract class AbstractMembershipFunction implements Serializable{

    protected double args[];

    public AbstractMembershipFunction(double... args){
        this.args = args;
    }

    public double[] getArgs() {
        return args;
    }

    public void setArgs(double... args){
        if (args.length == this.args.length){
            if ( !Double.isNaN(args[0]) && !Double.isNaN(args[1]))
                System.arraycopy(args,0,this.args,0,args.length);
        }

    }

    public abstract double f(double x,double sigma);

    public abstract double df(double x,double sigma);

    public abstract double dfa(double x,double sigma,int i);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AbstractMembershipFunction [");
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]+", ");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("]");
        return sb.toString();
    }
}
