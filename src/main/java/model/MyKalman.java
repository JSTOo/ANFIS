package model;

import main.Main;
import matrix.Matrix;
import matrix.operations.Multi;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Random;

/**
 * Created by Nixy on 31.03.2015.
 */
@Component
public class MyKalman extends NoLinearKalmanFIlter {



    static double ETA = Main.ETA;
    static double XI = 0.8;
    private final double k1 =  -1./MyModel.mu;
    private int t = 0;
    double step = Main.FILTER_STEP*Main.STEP;

    public MyKalman(List<Matrix> YList){
        super();
        this.YList = YList;
        DispEta = new Matrix(1./ETA);
        DispXi = Matrix.E(3);
        DispXi.setData(2,2,0.5);
        F = new Matrix(new double[][]{
                {0, step, 0},
                {0, 1, step},
                {0, 0, 1 - k1}});

        H = new Matrix(new double[][]{{1.,0./step,0}});
        G = new Matrix(1,1);
        calc();
    }

    private void calc() {
        Xf = new Matrix(3,1);
        Random r = new Random();
        Xf.setData(0,0,20+0*r.nextGaussian());
        Xf.setData(1,0,0*r.nextGaussian()*3+100);
        Xf.setData(2,0,0);
        Xo = Xf.getWithFactor(1);
        Kf = Matrix.E(3).getWithFactor(1E3);
        Ko = Kf.getWithFactor(1);

        XoList.add(Xo);
        KoList.add(Ko);
        XfList.add(Xf);
        KfList.add(Kf);
        for (; t < YList.size();t++ ) {

            getF();
            getH();
            getDispEta();
            getDispXi();
            getXf();
            getKf();
            getKo();
            getXo();
        }
    }

    @Override
    protected void getDispXi() {
    }

    @Override
    protected void getDispEta() {
    }

    @Override
    protected void getU() {

    }

    @Override
    protected void getF() {

    }

    @Override
    protected void getH() {

    }

    @Override
    protected Matrix getY() {
        return YList.get(t);
    }

    @Override
    protected Matrix getG() {
        G.setData(0,0,Xf.getData(0,0));
        GList.add(new Matrix(G));
        return G;
    }

    @Override
    protected Matrix getFx(Matrix X, Matrix U) {
        return Multi.calc(F,Xo);
    }
}
