package trash;

import memberships.GaussianMembershipFunction;
import memberships.SigmoidMembershipFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Nixy on 18.03.2015.
 */
public class MySugeno0Conclusion  {

    /**
     * dx1 dx2 dx3 - входные переменные. Приращения координаты
     * A1 - маленькое
     * A2 - среднее
     * A3 - большое
     * Наборы правил
     * 3^3 = 27 правил
         */
/*
    public static final double ETA = 0.05;

    private Sugeno0FuzzyConclusion sugeno;
    private Term[] terms;
    private double c[] = new double[]{
            0.1, 0.2 ,0.3, 0.2, 0.3, 0.2, 0.2, 0.3, 0.1,
            0.1, 0.2 ,0.2, 0.3, 0.4, 0.5, 0.3, 0.4, 0.6,
            0.2, 0.3 ,0.4, 0.3, 0.4, 0.6, 0.5, 0.7, 0.8
    };

    public MySugeno0Conclusion(){
        Term little = new Term("Маленькое", new SigmoidMembershipFunction(0.5,-10));
        Term middle = new Term("Среднее",new GaussianMembershipFunction(1.,0.5));
        Term large = new Term("Большое",new SigmoidMembershipFunction(1.5,10.));
        terms = new Term[]{little,middle,large};
        List<Sugeno0Rule> rules = new ArrayList<Sugeno0Rule>();
        for (int i = 0; i < 27; i++) {
            Term[] newTerms = new Term[3];
            int index = i;
            for (int j = 0; j < 3; j++) {
                newTerms[j] = terms[index%3];
                index /= 3;
            }

            rules.add(new Sugeno0Rule(newTerms,c[i]) {


                @Override
                public double logicConclusion(double[] x, double[] sigma) {
                    double al1 = this.getTerms().get(0).calc(x[0],sigma[0]);
                    double al2 = this.getTerms().get(1).calc(x[1],sigma[1]);
                    double al3 = this.getTerms().get(2).calc(x[2],sigma[2]);
                    if (al1 < al2){
                        if (al1 < al3){
                            setWinTerm(0);
                            return al1;
                        }else {
                            setWinTerm(2);
                            return al3;
                        }
                    }else {
                        if (al2 < al3){
                            setWinTerm(1);
                            return al2;
                        }else {
                            setWinTerm(2);
                            return al2;
                        }
                    }
                }

                @Override
                public String getName() {
                    StringBuilder sb = new StringBuilder("Если x1 = ").append(getTerms().get(0).getName())
                            .append(" и ").append(" x2 = ").append(getTerms().get(1).getName())
                            .append(" и ").append(" x3 = ").append(getTerms().get(2).getName())
                            .append(" тогда С = ").append(String.valueOf(getRuleExpertValue()));
                    name = sb.toString();
                    return name;
                }
            });
        }

        sugeno = new Sugeno0FuzzyConclusion(rules);
    }

    public Sugeno0FuzzyConclusion getSugeno() {
        return sugeno;
    }

    public Term[] getTerms() {
        return terms;
    }

    public void correct(double[]x,double[] sigma,double trueValue){
        double sa = sugeno.getAlphaSum();
        double sac = sugeno.getAlphaC();
        List<Term> terms = Arrays.asList(getTerms());
        double[] backupC = new double[sugeno.getRules().size()];
        for (int i = 0; i < backupC.length; i++) {
            Sugeno0Rule rule = (Sugeno0Rule) sugeno.getRules().get(i);
            backupC[i] = rule.getRuleExpertValue();
            System.out.println(rule.getName());
        }
        double[][] backupArgs = new double[][]{
                terms.get(0).getFunction().getArgs()
                ,terms.get(1).getFunction().getArgs()
                ,terms.get(2).getFunction().getArgs()};
        for (int i = 0; i < 3; i++) {
            Term term = terms.get(i);
            double[] args = term.getFunction().getArgs();
            System.out.println(term.getName() + " m = " + args[0] + " s = " + args[1]);
        }
        double dz = -trueValue+sac/sa;
        double dz0 = dz;
        int n = 0;
        System.out.println("Error = " + dz + " TrueValue = " + trueValue + " FuzzyValue = " + sac / sa);
        do {
            List<double[][]> deltap = new ArrayList<double[][]>();
            for (AbstractRule rule : sugeno.getRules()) {
                double sum1 = 0;
                Term winTerm = terms.get(terms.indexOf(rule.getTerms().get(rule.getWinTerm())));
                for (AbstractRule rule1  : sugeno.getRules()) {
                    Sugeno0Rule sugeno0Rule = (Sugeno0Rule) rule1;
                    sum1 += winTerm.getAlpha() * (sugeno0Rule.getRuleExpertValue() - sugeno0Rule.getRuleExpertValue());
                }
                double[][] delta = new double[3][3];
                delta[terms.indexOf(winTerm)][0] = -ETA / sa / sa * dz * sum1 * winTerm.getFunction().dfa(trueValue, 0);
                delta[terms.indexOf(winTerm)][1] = -ETA / sa / sa * dz * sum1 * winTerm.getFunction().dfa(trueValue, 1);
                delta[terms.indexOf(winTerm)][2] = -ETA * dz * winTerm.getAlpha() / sa;

                deltap.add(delta);
            }
            double dm[] = new double[3];
            double ds[] = new double[3];
            for (int i = 0; i < deltap.size(); i++) {
                Sugeno0Rule rule = (Sugeno0Rule) sugeno.getRules().get(i);
                double[][] delta = deltap.get(i);
                double c = rule.getRuleExpertValue() + delta[rule.getWinTerm()][2];
                rule.setRuleExpertValue(c);
                System.out.println(rule.getName()
                        + " delta [" + delta[0][0]+", " + delta[0][1]
                        + ", " + delta[1][0] + ", " + delta[1][1]
                        + ", " + delta[2][0] + ", " +delta[2][1] + "]");
                for (int j = 0; j < 3; j++) {
                    dm[j] += delta[j][0];
                    ds[j] += delta[j][1];
                }
            }
            for (int i = 0; i < 3; i++) {
                dm[i] /= deltap.size();
                ds[i] /= deltap.size();
                Term term = terms.get(i);
                double[] args = term.getFunction().getArgs();
                args[0] += dm[i];
                args[1] += ds[i];
                System.out.println(term.getName() + " deltaM = " + dm[i] + " m = " + args[0] + " deltaS =  " + ds[i] + " s = " + args[1]);
                term.getFunction().setArgs(args);
            }
            sugeno.f(x,sigma);
            sa = sugeno.getAlphaSum();
            sac = sugeno.getAlphaC();
            dz = -trueValue+sac/sa ;
            System.out.println("Error = " + dz + " TrueValue = " + trueValue + " FuzzyValue = " + sac/sa);
            if (Math.abs(dz) - Math.abs(dz0) > 0.1 ){
                for (int i = 0; i < backupC.length; i++) {
                    ((Sugeno0Rule)sugeno.getRules().get(i)).setRuleExpertValue(backupC[i]);
                }
                for (int i = 0; i < 3; i++) {
                    terms.get(i).getFunction().setArgs(backupArgs[i]);
                }
                break;
            }
            n++;
        }while (Math.abs(dz) > 0.05  && n < 1000);
        System.out.println("N = " + n);
    }*/

}
