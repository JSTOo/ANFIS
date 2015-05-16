package memberships;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Nixy on 07.03.2015.
 */
@Component
@Scope(value = "prototype")
public class SigmoidMembershipFunction extends AbstractMembershipFunction implements Serializable {


    public SigmoidMembershipFunction(double m,double sigma){
        super(m,sigma);
    }

    @Override
    public double f(double x,double sigma) {
        if (sigma == 0)
            return 1./(1+Math.exp(-args[1]*(x-args[0])));
        else {
            AbstractMembershipFunction function = sigma > 0.635 ? new NormalGaussianMembershipFunction(x, sigma) : new GaussianMembershipFunction(x,sigma);
            double alpha = Math.min(function.f(x, 0), 1./(1+Math.exp(-args[1]*(x-args[0]))));
            return alpha;
        }
    }

    @Override
    public double df(double x,double sigma) {
        return f(x,sigma)*(1-f(x,sigma));       // производная по аргументу
    }

    @Override
    public double dfa(double x,double sigma, int i) {
        switch (i){
            case 0 : return dfm(x,sigma); // производная по мат. ожиданию
            case 1 : return dfs(x,sigma); // производная по с.к.о.
            default:  throw new ArrayIndexOutOfBoundsException(i); // выход за пределы параметров
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SigmoidMembershipFunction [");
        sb.append(" m = " + args[0]);
        sb.append(" sigma = " + args[1]);
        sb.append("]");
        return sb.toString();
    }

    private double dfm(double x, double sigma) {
        if (sigma != 0) {
            AbstractMembershipFunction function = sigma > 0.635 ? new NormalGaussianMembershipFunction(x, sigma) : new GaussianMembershipFunction(x, sigma);
            double y1 = f(x,sigma);
            double y2 = function.f(x,0);
            if (y1 == y2)
                return 0;
        }
        return -args[1] * df(x, sigma);
    }

    private double dfs(double x, double sigma) {
        if (sigma != 0) {
            AbstractMembershipFunction function = sigma > 0.635 ? new NormalGaussianMembershipFunction(x, sigma) : new GaussianMembershipFunction(x, sigma);
            double y1 = f(x, sigma);
            double y2 = function.f(x,0);
            if (y1 == y2)
                return 0;
        }
        return (x-args[0])*df(x,sigma);
    }



}
