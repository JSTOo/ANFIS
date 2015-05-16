package trash;

import rules.AbstractFuzzyConclusion;
import rules.AbstractRule;

import java.io.Serializable;
import java.util.List;

/**
 * Нечеткий вывод на основе алгоритма Сугено 0-го порядка
 */
public class Sugeno0FuzzyConclusion extends AbstractFuzzyConclusion implements Serializable {

    private double alphaSum = 0;
    private double alphaC = 0;

    public Sugeno0FuzzyConclusion(List<Sugeno0Rule> rules){
        this.rules.addAll(rules);
    }

    @Override
    public double f(double[] args,double[] sigma) {
        alphaSum = 0;
        alphaC = 0;
        for (AbstractRule rule1 : rules){
            Sugeno0Rule rule = (Sugeno0Rule) rule1;
            double a = rule.logicConclusion(args,sigma);  // значение нечеткого вывода
            double c =  rule.getRuleExpertValue();  // значение правила
            alphaSum += a;                          // Сумма нечетких выводов
            alphaC += a*c;                      // Сумма нечетких выводов помноженная на значение правила
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
