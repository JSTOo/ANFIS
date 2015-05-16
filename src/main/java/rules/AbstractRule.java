package rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Nixy on 05.04.2015.
 */
public abstract class AbstractRule implements Serializable{
    private List<Term> terms = new ArrayList<Term>(); // список термов для правила
    protected String name; // словесное описание правила
    private int winTerm;

    public AbstractRule(Term[] args) {
        terms.addAll(new ArrayList<Term>(Arrays.asList(args)));
        getName();
    }

    /**
     * Логический вывод правила. Зависит от задачи обязателен к переопределению
     * @param x набор входов
     * @return вывод
     */
    public abstract double logicConclusion(double[] x,double[] sigma);

    /**
     * Формирование словесного описания правила
     * @return словесное описание
     */
    public abstract String getName();


    public List<Term> getTerms() {
        return terms;
    }

    public int getWinTerm() {
        return winTerm;
    }

    public void setWinTerm(int winTerm) {
        this.winTerm = winTerm;
    }

    @Override
    public String toString() {
        return getName();
    }
}
