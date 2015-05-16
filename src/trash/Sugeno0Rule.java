package trash;

import memberships.AbstractMembershipFunction;
import rules.AbstractRule;
import rules.Term;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Нечеткое правило
 */
public abstract class Sugeno0Rule extends AbstractRule implements Serializable {

    private double ruleExpertValue; // четкое значение правила

    public Sugeno0Rule(Term[] args, double ruleExpertValue){
        super(args);
        this.ruleExpertValue = ruleExpertValue;
    }

    public double getRuleExpertValue() {
        return ruleExpertValue;
    }

    public void setRuleExpertValue(double ruleExpertValue) {
        this.ruleExpertValue = ruleExpertValue;
    }


}
