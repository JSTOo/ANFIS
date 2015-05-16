package trash;

import memberships.SigmoidMembershipFunction;
import ru.javainside.genetic.system.*;
import rules.AbstractRule;
import rules.Conclusion;
import rules.Term;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Nixy on 08.04.2015.
 */
public class MySugeno0Conclusion2 implements Serializable,FitnessFunction,Mutation {
    /**
     * dx1 dx2 dx3 - входные переменные. Приращения координаты
     * A1 - маленькое
     * A2 - большое
     * Наборы правил
     * 2^3 = 8 правил
     */

    public static final double ETA = 0.1;
    private static final int TAIL = 50;

    private Sugeno0FuzzyConclusion sugeno;
    private Term[] terms;
    private double c[] = new double[]{
            0.0 ,0.4, 0.3, 0.4, 0.6, 0.5, 0.7, 1.8
    };

    List<Conclusion> conclusionArrayList = new ArrayList<Conclusion>();
    Deque<Conclusion> conclusions;
    Random r = new Random();

    public MySugeno0Conclusion2(){
        List<Sugeno0Rule> rules = new ArrayList<Sugeno0Rule>();
        for (int i = 0; i < 8; i++) {
            Term little = new Term("Маленькое", new SigmoidMembershipFunction(0.2,-10));
            Term large = new Term("Большое",new SigmoidMembershipFunction(0.5,10.));
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

    public void correct(double[]x,double[] sigma,double trueValue){
        double sa = sugeno.getAlphaSum();
        double sac = sugeno.getAlphaC();
        double z = sac/sa;

        if (conclusionArrayList != null) {
            this.conclusionArrayList.add(new Conclusion(x, sigma, trueValue, z));  // запоминаем новое решение
        }else {
            conclusionArrayList = new ArrayList<Conclusion>();
            this.conclusionArrayList.add(new Conclusion(x, sigma, trueValue, z));
        }
        List<Conclusion> tmp = new ArrayList<Conclusion>();
        for (int i = 0, j = this.conclusionArrayList.size()-1; i < TAIL && j > -1; i++,j--) {
            tmp.add(this.conclusionArrayList.get(j));
        }

        conclusions = new ArrayDeque<Conclusion>();

        conclusions.add(tmp.remove(tmp.size() - 1));
        int size = tmp.size();

        for (int i = 0; i < size && i < TAIL-1; i++) {
            int p = r.nextInt(tmp.size());
            conclusions.add(tmp.remove(p));
        }

        PersonFactory factory = new SimpleFactory();
        List<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < 20; i++) {
            persons.add(factory.revivePerson(
                     new SimpleMutation(30)
                    ,this
                    ,new SimpleCrossover()
                    ,codeChromosome(sugeno)));
        }
        Populations populations = new SimplePopulations(new Population(persons),factory);
        Person person = persons.get(0);
        int i = 0;
        while (person.getFitness() > 1){
            Person bestPerson = GeneticUtils.getBestPerson(populations.getLastPopulation());
            System.out.println("Population " + (++i) + ". Best person: " + bestPerson);
            System.out.println("Fitness mean: " + GeneticUtils.getFitnessMean(populations.getLastPopulation()));
            if (bestPerson.getFitness() < person.getFitness()) {
                person = bestPerson;
            }
            populations.nextGen();
        }
        System.out.println("Population " + (++i) + ". Best person: " + person);
        System.out.println("Fitness mean: " + GeneticUtils.getFitnessMean(populations.getLastPopulation()));
        decodeChromosome(person.getChromosome());

    }


    @Override
      public double getFitness(Person person) {
        Chromosome<Double> chromosome = person.getChromosome();
        decodeChromosome(chromosome);
        for (Conclusion conclusion : conclusions){
            conclusion.setZ(sugeno.f(conclusion.getInputPoint(), conclusion.getSigma()));
        }

        return getSumErr(conclusions);
    }

    private double getSumErr(Deque<Conclusion> conclusions){
        double sumErr = 0;
        for (Conclusion conclusion : conclusions){
            sumErr += conclusion.getErr();
        }
        return sumErr/conclusions.size();
    }

    private Chromosome<Double> codeChromosome(Sugeno0FuzzyConclusion sugeno) {
        List<Double> chromosome = new ArrayList<Double>();
        for (int i = 0; i < sugeno.getRules().size(); i++) {
            Sugeno0Rule rule = (Sugeno0Rule) sugeno.getRules().get(i);
            for (int j = 0; j < rule.getTerms().size(); j++) {
                Term term = rule.getTerms().get(j);
                double[] args = term.getFunction().getArgs();
                chromosome.add(new Double(args[0]));
                chromosome.add(new Double(args[1]));

            }
            chromosome.add(new Double(rule.getRuleExpertValue()));
        }
        return new Chromosome<Double>(chromosome);
    }

    private void decodeChromosome(Chromosome<Double> chromosome){
        int ruleCount = sugeno.getRules().size();
        for (int i = 0; i < ruleCount; i++) {
            Sugeno0Rule rule = (Sugeno0Rule) sugeno.getRules().get(i);
            int termCount = rule.getTerms().size();
            for (int j = 0; j < termCount; j++) {
                Term term = rule.getTerms().get(j);
                int index = i*termCount*2+j*2+i;
                term.getFunction().setArgs(chromosome.getGene(index),chromosome.getGene(index+1));
            }
            rule.setRuleExpertValue(chromosome.getGene(termCount*2*(i+1)));
        }
    }

    public void clearConclusion(){
        conclusions = null;
    }

    @Override
    public void mutation(Person person) {
        Chromosome<Double> chromosome = person.getChromosome();
        Random r = new Random();
        for (int i = 0; i < getRules().size(); i++) {
           chromosome.setGene((i + 1) * 6,Math.abs(chromosome.getGene((i + 1) * 6) + r.nextGaussian()));
        }
    }
}
