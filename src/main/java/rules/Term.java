package rules;

import memberships.AbstractMembershipFunction;
import memberships.GaussianMembershipFunction;
import memberships.GoldenSectionSearch;
import memberships.NormalGaussianMembershipFunction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Терм
 */
@Component
@Scope(value = "prototype")
public class Term implements Serializable {
    private String name;    // словестное описание
    private AbstractMembershipFunction function; // функция принадлежности
    private double alpha;


    public Term(String name, AbstractMembershipFunction function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public void setFunction(AbstractMembershipFunction function) {
        this.function = function;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double calc(double x){
        alpha = function.f(x,0);
        return  alpha;
    }

    public double calc(double x,double sigma){
        AbstractMembershipFunction function = sigma > 0.635 ? new NormalGaussianMembershipFunction(x, sigma) : new GaussianMembershipFunction(x,sigma);
        //NormalGaussianMembershipFunction function =
        if (sigma != 0) {
            double xOpt = GoldenSectionSearch.search(new AbstractMembershipFunction[]{function,this.function}
                    , new double[]{-3*sigma+x,3*sigma+x}
                    ,1e-5);
            alpha =  Math.min(function.f(xOpt,sigma),this.function.f(xOpt,sigma));
            double alphaMean = Math.min(function.f(x,sigma),this.function.f(x,sigma));
            return Math.max(alphaMean,alpha);
        } else {
            return calc(x);
        }
    }

    public double getAlpha() {
        return alpha;
    }

    public AbstractMembershipFunction getFunction() {
        return function;
    }
}
