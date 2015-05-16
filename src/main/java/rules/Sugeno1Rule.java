package rules;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Nixy on 09.04.2015.
 */
@Component
@Scope(value = "prototype")
public abstract class Sugeno1Rule extends AbstractRule implements Serializable {
    private double ruleExpertValue[]; // четкое значение правила

    public Sugeno1Rule(Term[] args, double ruleExpertValue[]){
        super(args);
        setRuleExpertValue(ruleExpertValue);
    }

    public double[] getRuleExpertValue() {
        return ruleExpertValue;
    }

    public void setRuleExpertValue(double[] ruleExpertValue) {
        this.ruleExpertValue = new double[ruleExpertValue.length];
        System.arraycopy(ruleExpertValue,0,this.ruleExpertValue,0,ruleExpertValue.length);
    }
}
