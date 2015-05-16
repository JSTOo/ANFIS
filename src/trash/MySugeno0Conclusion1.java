package trash;

import memberships.SigmoidMembershipFunction;
import rules.*;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Nixy on 18.03.2015.
 */
public class MySugeno0Conclusion1 implements Serializable {

    /**
     * dx1 dx2 dx3 - входные переменные. Приращения координаты
     * A1 - маленькое
     * A2 - большое
     * Наборы правил
     * 2^3 = 8 правил
     */

    public static final double ETA = 0.01;
    private static final int TAIL = 50;

    private Sugeno0FuzzyConclusion sugeno;
    private ConclusionSetFabric fabric = new LinearConclusionSet();
  //  private ConclusionSetFabric fabric = new RandomConclusionSet();
    private Term[] terms;
    private double c[] = new double[]{
             19.2 ,19.4, 20.3, 20.4, 20.6, 20.5, 20.7, 21.
    };

    List<Conclusion> conclusions = new ArrayList<Conclusion>();
    Random r = new Random();

    public MySugeno0Conclusion1(){
        List<Sugeno0Rule> rules = new ArrayList<Sugeno0Rule>();
        for (int i = 0; i < 8; i++) {
            Term little = new Term("Маленькое", new SigmoidMembershipFunction(0.2+19,-5));
            Term large = new Term("Большое",new SigmoidMembershipFunction(0.5+21,5.));
            terms = new Term[]{little,large};
            Term[] newTerms = new Term[3];
            int index = i;
            for (int j = 0; j < 3; j++) {
                newTerms[j] = terms[index%2];
                index /= 2;
            }

            rules.add(new Sugeno0Rule(newTerms,c[i]) {

                @Override
                public double logicConclusion(double[] x, double[] sigma) {
                    double alpha = 1;
                    for (int j = 0; j < this.getTerms().size(); j++) {
                        double a = this.getTerms().get(j).calc(x[j],sigma[j]);
                        if (a < alpha){
                            alpha = a;
                            setWinTerm(j);
                        }
                    }
                    return  alpha;
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

    public List<AbstractRule> getRules(){
        return sugeno.getRules();
    }

    public double f(double[] x,double[] sigma){
        return sugeno.f(x,sigma);
    }


    public void correct(double[]x,double[] sigma,double trueValue,double sigmaTrueValue){
        double sa = sugeno.getAlphaSum();
        double sac = sugeno.getAlphaC();

        double[] backupC = new double[sugeno.getRules().size()];
        double[][][] backupArgs = new double[sugeno.getRules().size()][3][2];
        for (int i = 0; i < backupC.length; i++) {
            Sugeno0Rule rule = (Sugeno0Rule)sugeno.getRules().get(i);
            backupC[i] = rule.getRuleExpertValue();
            for (int j = 0; j < 3; j++) {
                Term term = rule.getTerms().get(j);
                double[] args = term.getFunction().getArgs();
                backupArgs[i][j][0] = args[0];
                backupArgs[i][j][1] = args[1];
            }
        }
        double z = sac/sa;
        double dz = -trueValue + z;

        if (conclusions != null) {
            this.conclusions.add(new Conclusion(x, sigma, trueValue, z));  // запоминаем новое решение
        }else {
            conclusions = new ArrayList<Conclusion>();
            fabric = new LinearConclusionSet();
            this.conclusions.add(new Conclusion(x, sigma, trueValue, z));
        }
        List<Conclusion> tmp = new ArrayList<Conclusion>();
        for (int i = 0, j = this.conclusions.size()-1; i < TAIL && j > -1; i++,j--) {
            tmp.add(this.conclusions.get(j));
        }

        Deque<Conclusion> conclusions = fabric.makeSet(tmp,TAIL);
        double[][] backUpZ = new double[conclusions.size()][2];
        for (int i = 0; i < conclusions.size(); i++) {
            Conclusion conclusion = conclusions.pollFirst();
            backUpZ[i][0] = conclusion.getZ();
            backUpZ[i][1] = conclusion.getTrueValue();
            conclusions.add(conclusion);
        }

        int n = 0;
        int m = 0;
        double dz0 = getSumErr(conclusions);
        while (Math.abs(dz) > 0.01  && n < 1000 && m < 10000) {
            Conclusion conclusion = conclusions.pollFirst();
            sugeno.f(conclusion.getInputPoint(),conclusion.getSigma());
            sa = sugeno.getAlphaSum();
            sac = sugeno.getAlphaC();
            dz = -conclusion.getTrueValue()+sac/sa ;

            for (AbstractRule arule : sugeno.getRules()) {
                Sugeno0Rule rule = (Sugeno0Rule) arule;
                double sum1 = 0;
                Term winTerm = rule.getTerms().get(rule.getWinTerm());
                for (AbstractRule arule1 : sugeno.getRules()) {
                    Sugeno0Rule rule1 = (Sugeno0Rule) arule1;
                    sum1 += winTerm.getAlpha() * (rule.getRuleExpertValue() - rule1.getRuleExpertValue());
                }
                double[] args = winTerm.getFunction().getArgs();
                double difm = winTerm.getFunction().dfa(trueValue,sigmaTrueValue, 0);
                double dm = -(ETA) / sa / sa * dz * sum1 * difm;
                double difs =  winTerm.getFunction().dfa(trueValue,sigmaTrueValue, 1);
                double ds = -(ETA) / sa / sa * dz * sum1 * difs;
                if (Double.isNaN(dm) || Double.isNaN(ds)){
                    break;
                }
                args[0] += dm;
                args[1] += ds;

                winTerm.getFunction().setArgs(args);
                double dc = - (ETA )* dz * winTerm.getAlpha() / sa;
                if (Double.isNaN(dc)){
                    break;
                }
                rule.setRuleExpertValue(rule.getRuleExpertValue() + dc );
            }


            conclusion.setZ(sugeno.f(conclusion.getInputPoint(), conclusion.getSigma()));
            conclusions.add(conclusion);
            n++;
            dz = getSumErr(conclusions);
            if( Math.abs(dz-dz0) > 0.01 ){
                for (int i = 0; i < backupC.length; i++) {
                    Sugeno0Rule rule = (Sugeno0Rule) sugeno.getRules().get(i);
                    rule.setRuleExpertValue(backupC[i]);
                    for (int j = 0; j < 3; j++) {
                        Term term = rule.getTerms().get(j);
                        term.getFunction().setArgs(backupArgs[i][j]);
                    }

                }
                int j = 0;
                for (int i = 0; j < backUpZ.length; i++) {
                    if (i == backUpZ.length)  i = 0;
                    while (true){
                        conclusion = conclusions.pollFirst();
                        if (conclusion.getTrueValue() == backUpZ[i][1]){
                            conclusion.setZ(backUpZ[i][0]);
                            conclusions.add(conclusion);
                            j++;
                            break;
                        }
                        conclusions.add(conclusion);

                    }

                }
                System.out.print("Back up ");
                break;
            }else{
                for (int i = 0; i < backupC.length; i++) {
                    Sugeno0Rule rule = (Sugeno0Rule)sugeno.getRules().get(i);
                    backupC[i] = rule.getRuleExpertValue();
                    for (int j = 0; j < 3; j++) {
                        Term term = rule.getTerms().get(j);
                        double[] args = term.getFunction().getArgs();
                        backupArgs[i][j][0] = args[0];
                        backupArgs[i][j][1] = args[1];
                    }
                }
                for (int i = 0; i < conclusions.size(); i++) {
                    conclusion = conclusions.pollFirst();
                    backUpZ[i][0] = conclusion.getZ();
                    backUpZ[i][1] = conclusion.getTrueValue();
                    conclusions.add(conclusion);
                }
            }
            if (n == 1000 && dz > 0.2){
                n = 0;
            }
            m++;
        }
        System.out.println("Sum error : " + getSumErr(conclusions) + " err0 " + dz0 + " " + m);
        System.out.println("Conclusion : " + this.conclusions.get(this.conclusions.size()-1).getZ() + " Measure: " + trueValue );

    }

    private double getSumErr(Deque<Conclusion> conclusions){
        double sumErr = 0;
        for (Conclusion conclusion : conclusions){
            sumErr += conclusion.getErr();
        }
        return sumErr/conclusions.size();
    }

    public void clearConclusion(){
        fabric = null;
        conclusions = null;
    }

}
