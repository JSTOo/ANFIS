package memberships;

import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Created by Nixy on 07.03.2015.
 */
@Service
public final class GoldenSectionSearch implements Serializable {

    private GoldenSectionSearch(){}

    public static final Double FI = (1+Math.sqrt(5))/2;

    public static double search(AbstractMembershipFunction f[],double[] space,double eps){
        double a = space[0],b = space[1];
        do{
            double x1 = b - (b - a)/FI;
            double x2 = a + (b - a)/FI;
            double y1 = Math.abs(f[0].f(x1,0)-f[1].f(x1,0));
            double y2 = Math.abs(f[0].f(x2,0)-f[1].f(x2,0));
            if (y1 > y2) a = x1; else  b = x2;
        }while (Math.abs(b-a)>eps);
        return (a+b)/2;
    }
}
