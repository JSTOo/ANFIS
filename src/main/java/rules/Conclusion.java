package rules;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by Nixy on 02.04.2015.
 */
@Component
@Scope(value = "prototype")
public class Conclusion implements Serializable {

    double[] inputPoint; // dx1,dx2,dx3
    double[] sigma;
    double trueValue;
    double z;

    public Conclusion(double[] inputPoint,double[] sigma, double trueValue, double z) {
        this.inputPoint = new double[inputPoint.length];
        this.sigma = new double[sigma.length];
        System.arraycopy(inputPoint,0,this.inputPoint,0,inputPoint.length);
        System.arraycopy(sigma,0,this.sigma,0,sigma.length);
        this.trueValue = trueValue;
        this.z = z;
    }

    public double getErr(){
        return Math.abs(trueValue-z);
    }

    public double[] getInputPoint() {
        return inputPoint;
    }

    public double[] getSigma() {
        return sigma;
    }

    public double getTrueValue() {
        return trueValue;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
