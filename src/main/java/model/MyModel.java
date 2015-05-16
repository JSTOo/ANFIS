package model;

import matrix.Matrix;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Nixy on 18.03.2015.
 */
@Component
public class MyModel extends AbstractModel implements Serializable{

    public static final double mu = 50;
    public static final double D = 0.01;

    double n;
    Random r = new Random();
    public MyModel(Matrix initCondition, double step) {
        super(initCondition, step);

        n = r.nextGaussian()*1./Math.sqrt(step);
    }

    @Override
    public boolean isEnough(Object... objects) {
        return false;
    }

    @Override
    public Matrix F(double t, Matrix X) {
        double x = X.getData(0,0);
        double vx = X.getData(1,0);
        double ax = X.getData(2,0);

        double dx = vx;
        double dax = -1./mu*ax + Math.sqrt(D*mu)*n;
        double dvx = ax;//Math.sin(dax*t);0;//



        X.setData(0,0,dx);
        X.setData(1,0,dvx);
        X.setData(2,0,dax);
        return X;
    }

    @Override
    public void setX(Matrix X) {
        super.setX(X);
        n = r.nextGaussian()*1./Math.sqrt(step);
    }
}
