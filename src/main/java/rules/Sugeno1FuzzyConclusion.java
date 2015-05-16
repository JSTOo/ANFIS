package rules;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Nixy on 09.04.2015.
 */
@Component
public class Sugeno1FuzzyConclusion extends AbstractFuzzyConclusion implements Serializable {

    private double alphaSum = 0;
    private double alphaC = 0;

    public Sugeno1FuzzyConclusion(List<Sugeno1Rule> rules){
        this.rules.addAll(rules);
    }

    @Override
    public double f(double[] args,double[] sigma) {
        alphaSum = 0;
        alphaC = 0;
        for (AbstractRule arule : rules){
            Sugeno1Rule rule = (Sugeno1Rule) arule;
            double a = rule.logicConclusion(args,sigma);  // значение нечеткого вывода
            double[] k =  rule.getRuleExpertValue();  // значение правила
            double c = 0;
            for (int i = 1; i < k.length; i++) {
                c += k[i]*args[i-1];
            }
            c += k[0];
            alphaC += c*a;                 // Сумма нечетких выводов помноженная на значение правила
            alphaSum += a;                          // Сумма нечетких выводов

        }

        return alphaC/alphaSum;
    }


    public double getAlphaC() {
        return alphaC;
    }

    public double getAlphaSum() {
        return alphaSum;
    }
}