package rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный класс нечеткого вывода
 */
public abstract class AbstractFuzzyConclusion implements Serializable {

    protected List<AbstractRule> rules = new ArrayList<AbstractRule>(); // набор правил

    /**
     * нечеткий вывод
     * @param args вход
     * @return вывод
     */
    public abstract double f(double[] args,double[] sigma);

    /**
     * метод получения набора правил
     * @return правила
     */
    public List<AbstractRule> getRules(){
        return rules;
    }
}
