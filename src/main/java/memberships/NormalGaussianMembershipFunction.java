package memberships;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Nixy on 19.03.2015.
 */
@Component
@Scope(value = "prototype")
public class NormalGaussianMembershipFunction extends AbstractMembershipFunction implements Serializable {

    public NormalGaussianMembershipFunction(double m,double sigma){
        super(m,sigma);
    }

    @Override
    public double f(double x,double sigma) {
        if (sigma == 0)
            return 1./Math.sqrt(Math.PI)/args[1]*Math.exp(-Math.pow((x-args[0])/args[1],2)/2);
        else {
            AbstractMembershipFunction function = sigma > 0.635 ? new NormalGaussianMembershipFunction(x, sigma) : new GaussianMembershipFunction(x,sigma);
            double alpha = Math.min(function.f(x, 0), 1./Math.sqrt(Math.PI)/args[1]*Math.exp(-Math.pow((x-args[0])/args[1],2)/2));
            return alpha;
        }
    }

    @Override
    public double df(double x,double sigma) {
        return -2*(x-args[0])/Math.pow(args[1],2)*f(x,sigma);
    }

    @Override
    public double dfa(double x,double sigma, int i) {
        switch (i){
            case 0 : return -df(x,sigma); // производная по мат. ожиданию
            case 1 : return -((x-args[0])*df(x,sigma)+Math.PI/2*f(x,sigma))/args[1]; // производная по с.к.о.
            default:  throw new ArrayIndexOutOfBoundsException(i); // выход за пределы параметров
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NormalGaussianMembershipFunction [");
        sb.append(" m = " + args[0]);
        sb.append(" sigma = " + args[1]);
        sb.append("]");
        return sb.toString();
    }
}
