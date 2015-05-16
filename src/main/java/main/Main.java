package main;

import form.MainForm;
import integrators.RungeKutt;
import matrix.Matrix;
import model.MyKalman;
import model.MyModel;
import rules.AbstractRule;
import rules.MySugeno1Conclusion;
import rules.Term;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

public class Main {

    public static final double STEP = 0.1;
    public static final double END = 50;
    public static final int FILTER_STEP = 2;
    public static double ETA = 3;

    public static void main(String[] args) {
        Random r = new Random();
        MainForm mainForm = new MainForm("Нейро-нечеткий фильтр");
       // MySugeno0Conclusion1 mySugeno = null;
        MySugeno1Conclusion mySugeno = null;
        List<Matrix> Y = null;
        List<Matrix> X = null;
        List<Matrix> D = null;
        try {
            FileInputStream fis = new FileInputStream("sugeno.out");
            ObjectInputStream ois = new ObjectInputStream(fis);
          //  mySugeno = (MySugeno0Conclusion1) ois.readObject();
        //   mySugeno = (MySugeno1Conclusion) ois.readObject();
            fis = new FileInputStream("Y.out");
            ois = new ObjectInputStream(fis);
            Y = (List<Matrix>) ois.readObject();
            fis = new FileInputStream("X.out");
            ois = new ObjectInputStream(fis);
            X = (List<Matrix>) ois.readObject();
            fis = new FileInputStream("D.out");
            ois = new ObjectInputStream(fis);
            D = (List<Matrix>) ois.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mySugeno == null){
         //   mySugeno = new MySugeno0Conclusion1();
            mySugeno = new MySugeno1Conclusion();
        }

        if (Y == null) {
            Matrix init = new Matrix(3, 1);
            init.setData(1, 0, 110);
            MyModel m = new MyModel(init, STEP);
            RungeKutt rungeKutt = new RungeKutt(m, 0, END, STEP);
            rungeKutt.integrate();  // Инициализация модели и интергирование
            X = m.getX();
            Y = new ArrayList<Matrix>();
            D = new ArrayList<Matrix>();
            for (int i = FILTER_STEP; i < X.size(); i+=FILTER_STEP) {   // Получение измерений
                D.add(new Matrix(X.get(i).getData(0, 0) - X.get(i - FILTER_STEP).getData(0, 0)));
                Y.add(new Matrix(D.get(D.size() - 1).getData(0, 0) + r.nextGaussian() * ETA));
            }
        }
        for(AbstractRule rule : mySugeno.getRules()){
            System.out.println(rule.getName());
            for (Term term : rule.getTerms()) {
                System.out.println("m = " + term.getFunction().getArgs()[0]
                        + " s = " + term.getFunction().getArgs()[1]);
            }
        }
         // Процесс вычисления нечеткой системы
         double[] sigma = new double[]{ETA,ETA,ETA};
      //    double[] sigma = new double[]{0,0,0};
      //  double[] sigma = new double[]{0.6351,0.6351,0.6351};
        List<Matrix> bXf = new ArrayList<Matrix>();
        List<Matrix> bDeltaf = new ArrayList<Matrix>();
        double xf = 0;
        bDeltaf.add(new Matrix(Y.get(0).getData(0,0)));
        bDeltaf.add(new Matrix(Y.get(1).getData(0,0)));
        bDeltaf.add(new Matrix(Y.get(2).getData(0,0)));
        bXf.add(new Matrix(xf += Y.get(0).getData(0,0)));
        bXf.add(new Matrix(xf += Y.get(1).getData(0,0)));
        bXf.add(new Matrix(xf += Y.get(2).getData(0,0)));
        for (int i = 2; i < Y.size()-1; i++) {
            double[] x =  new double[]{
                    abs(Y.get(i-2).getData(0,0))
                    ,abs(Y.get(i-1).getData(0,0))
                    ,abs(Y.get(i).getData(0,0))
            };

            double sign = signum(signum(Y.get(i).getData(0,0))+signum(Y.get(i-1).getData(0,0))+signum(Y.get(i-2).getData(0,0)));
            double delta = sign*mySugeno.f(x,sigma);
            bDeltaf.add(new Matrix(delta));
            bXf.add(new Matrix(xf += delta));
        }

        List<Matrix> Xf = new ArrayList<Matrix>();
        List<Matrix> Deltaf = new ArrayList<Matrix>();
        xf = 0;
        Deltaf.add(new Matrix(Y.get(0).getData(0,0)));
        Deltaf.add(new Matrix(Y.get(1).getData(0,0)));
        Deltaf.add(new Matrix(Y.get(2).getData(0,0)));
        Xf.add(new Matrix(xf += Y.get(0).getData(0,0)));
        Xf.add(new Matrix(xf += Y.get(1).getData(0,0)));
        Xf.add(new Matrix(xf += Y.get(2).getData(0,0)));
        for (int i = 2; i < Y.size(); i++) { // Процесс вычисления нечеткой системы и ее корректировка
            double[] x =  new double[]{
                    abs(Y.get(i-2).getData(0,0))
                    ,abs(Y.get(i-1).getData(0,0))
                    ,abs(Y.get(i).getData(0,0))
            };
            double sign = signum(signum(Y.get(i).getData(0,0))+signum(Y.get(i-1).getData(0,0))+signum(Y.get(i-2).getData(0,0)));
            double delta = sign*mySugeno.f(x,sigma);
            Xf.add(new Matrix(xf += delta));
            Deltaf.add(new Matrix(delta));
            if( i+1 < Y.size())
               mySugeno.correct(x,sigma,Math.abs(Y.get(i+1).getData(0,0)),sigma[0]);
        }
        mySugeno.clearConclusion();

        for(AbstractRule rule : mySugeno.getRules()){
            System.out.println(rule.getName());
            for (Term term : rule.getTerms()) {
                System.out.println("m = " + term.getFunction().getArgs()[0]
                        + " s = " + term.getFunction().getArgs()[1]);
            }
        }

        MyKalman kalman = new MyKalman(Y);
        xf = 0;
        ArrayList<Matrix> dXk = new ArrayList<Matrix>();
        ArrayList<Matrix> dKalman = new ArrayList<Matrix>();
        ArrayList<Matrix> p3S = new ArrayList<Matrix>();
        ArrayList<Matrix> m3S = new ArrayList<Matrix>();

        List<Matrix> xoList = kalman.XoList;
        List<Matrix> errX = new ArrayList<Matrix>();
        List<Matrix> errXk = new ArrayList<Matrix>();
        List<Matrix> errbX = new ArrayList<Matrix>();
        List<Matrix> errZ = new ArrayList<Matrix>();
        List<Matrix> errY = new ArrayList<Matrix>();
        for (int i1 = 0; i1 < xoList.size()-1; i1++) {
            Matrix xk = xoList.get(i1);
            xf += xk.getData(0, 0);
            dXk.add(new Matrix(xf));
            dKalman.add(new Matrix(D.get(i1).getData(0,0)-xk.getData(0,0)));
            double sigmaK = Math.sqrt(kalman.KoList.get(i1).getData(0,0));
            p3S.add(new Matrix(3*sigmaK));
            m3S.add(new Matrix(-3*sigmaK));

        }
        for (int i = 0; i < bDeltaf.size(); i++) {
            errX.add(new Matrix(D.get(i).getData(0,0)-Deltaf.get(i).getData(0,0)));
            errbX.add(new Matrix(D.get(i).getData(0, 0)-bDeltaf.get(i).getData(0,0)));
            errXk.add(new Matrix(D.get(i).getData(0,0)-xoList.get(i).getData(0,0)));
            errZ.add(new Matrix(0.));
            errY.add(new Matrix(D.get(i).getData(0,0)-Y.get(i).getData(0,0)));
        }

        List<Integer> index = new ArrayList<Integer>();
        index.add(0);
        index.add(0);
        index.add(0);
        index.add(0);
        index.add(0);
        List<Double> xStep = Arrays.asList(STEP,STEP*FILTER_STEP,STEP*FILTER_STEP,STEP*FILTER_STEP,STEP*FILTER_STEP);
        List<Double> delStep = Arrays.asList(STEP*FILTER_STEP,STEP*FILTER_STEP,STEP*FILTER_STEP,STEP*FILTER_STEP,STEP*FILTER_STEP,STEP*FILTER_STEP);
        List<List<Matrix>> cordX = new ArrayList<List<Matrix>>();
        List<String> cordXNames = new ArrayList<String>();
        List<String> cordX2Names = new ArrayList<String>();
        List<List<Matrix>> cordX2 = new ArrayList<List<Matrix>>();
        List<List<Matrix>> errcordX = new ArrayList<List<Matrix>>();
        List<String> errNames = new ArrayList<String>();
        cordX.add(X);cordXNames.add("Истинное");
        cordX.add(Xf);cordXNames.add("Оценка FIS");
        cordX.add(bXf);cordXNames.add("Оценка FIS без обучения");

        cordX2.add(X);cordX2Names.add("Истинное");
        cordX2.add(dXk);cordX2Names.add("Оценка ФК");

        errcordX.add(errbX); errNames.add("Ошибка FIS до обучения");
        errcordX.add(errX); errNames.add("Ошибка FIS");
        errcordX.add(errXk); errNames.add("Ошибка Фильтра Калмана");
        errcordX.add(errY); errNames.add("Ошибка Измерения");
        errcordX.add(errZ); errNames.add("");

        List<List<Matrix>> kalmanList = new ArrayList<List<Matrix>>();
        List<String> kalmanNames = new ArrayList<String>();
        kalmanList.add(dKalman);kalmanNames.add("Оценка");
        kalmanList.add(p3S);kalmanNames.add("+3s");
        kalmanList.add(m3S);kalmanNames.add("-3s");


        List<List<Matrix>> del = new ArrayList<List<Matrix>>();
        List<String> delNames = new ArrayList<String>();
        del.add(Y); delNames.add("Измерение");
        del.add(Deltaf);    delNames.add("Решение FIS");
        del.add(kalman.XoList); delNames.add("Решение Фильтра Калмана");
        del.add(bDeltaf);   delNames.add("Решение FIS до обучения");


        List<Color> colors = new ArrayList<Color>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.MAGENTA);
        colors.add(Color.ORANGE);

        mainForm.addGraphic("X", MainForm.drawChartT("X", X, 0, STEP));
        mainForm.addGraphic("VX", MainForm.drawChartT("VX", X, 1, STEP));
        mainForm.addGraphic("AX", MainForm.drawChartT("AX", X, 2, STEP));
        mainForm.addGraphic("Y", MainForm.drawChartT("Y", Y, 0, STEP*FILTER_STEP));
        mainForm.addGraphic("dXf", MainForm.drawChartT("dXf", Deltaf, 0, STEP*FILTER_STEP));
        mainForm.addGraphic("bdXf", MainForm.drawChartT("bdXf", bDeltaf, 0, STEP*FILTER_STEP));
        mainForm.addGraphic("kalman", MainForm.drawNChartT("kalman", kalmanList, index, 0, delStep,colors,kalmanNames));

        mainForm.addGraphic("",MainForm.drawNChartT("",del,index,0,delStep,colors,delNames));
        mainForm.addGraphic("CoordX",MainForm.drawNChartT("CoordX",cordX,index,0,xStep,colors,cordXNames));
        mainForm.addGraphic("CoordX2",MainForm.drawNChartT("CoordX",cordX2,index,0,xStep,colors,cordX2Names));
        mainForm.addGraphic("errCoordX",MainForm.drawNChartT("errCoordX",errcordX,index,0,delStep,colors,errNames));
        double err = 0;
        double errb = 0;
        double errK = 0;
        double errYd = 0;
        for (int i = 0; i < Deltaf.size()-1; i++) {
            err +=  Math.pow(D.get(i).getData(0,0)-Deltaf.get(i).getData(0,0),2);
            errb +=  Math.pow(D.get(i).getData(0,0)-bDeltaf.get(i).getData(0,0),2);
            errK +=  Math.pow(D.get(i).getData(0,0)-xoList.get(i).getData(0,0),2);
            errYd +=  Math.pow(D.get(i).getData(0,0)-Y.get(i).getData(0,0),2);
        }
        System.out.println("Sqr Error = " +err/(Xf.size()-1));
        System.out.println("Sqr Error Before = " + errb / (Xf.size() - 1));
        System.out.println("Sqr Error Kalman = " +errK/(Xf.size()-1));
        System.out.println("Sqr Error Y = " +errYd/(Xf.size()-1));
        try {
            FileOutputStream fos = new FileOutputStream("sugeno.out");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mySugeno);
            oos.flush();
            oos.close();
            fos = new FileOutputStream("Y.out");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(Y);
            fos = new FileOutputStream("X.out");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(X);
            fos = new FileOutputStream("D.out");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(D);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainForm.setVisible(true);
    }
}
