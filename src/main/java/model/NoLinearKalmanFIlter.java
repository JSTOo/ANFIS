package model;

import matrix.Matrix;
import matrix.operations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nixy
 * Date: 18.11.13
 * Time: 7:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class NoLinearKalmanFIlter {
    /*       Xo - оценка
    *        Ko - ковариация ошибок оценки
    *        Xf - прогноз
    *        Kf - ковариация ошибок прогноза
    *
    *
    *        F - фундументальная матрица
    *        H - матрица наблюдаймости
    *        U - неслучайное воздействие
    *
    *        DispXi - дисперсии сдучайных воздействий
    *        DispEta - дисперсии ошибок измерений , должна быть заранее обращена
    *
    * */


    protected Matrix Xo;
    protected Matrix Ko;
    protected Matrix Xf;
    protected Matrix Kf;
    protected Matrix U;
    protected Matrix F;
    protected Matrix H;
    protected Matrix DispXi;
    protected Matrix DispEta;
    protected Matrix Y;
    protected Matrix G;

    public List<Matrix> XoList = new ArrayList<Matrix>();
    public List<Matrix> KoList = new ArrayList<Matrix>();
    public List<Matrix> XfList = new ArrayList<Matrix>();
    public List<Matrix> KfList = new ArrayList<Matrix>();
    public List<Matrix> YList = new ArrayList<Matrix>();
    public List<Matrix> GList = new ArrayList<Matrix>();
    public List<Matrix> DList = new ArrayList<Matrix>();  // вектор невязок по измерениям


    protected abstract void getDispXi();
    protected abstract void getDispEta();
    protected abstract void getU();


    protected abstract void getF();
    protected abstract void getH();
    protected abstract Matrix getY();
    protected abstract Matrix getG();
    protected abstract Matrix getFx(Matrix X,Matrix U);

    protected void getXo() {
        Matrix HT = Transpose.calc(H);
        Matrix tmp = Multi.calc(Ko, HT);       // P* * HT
        Matrix tmp4 = Multi.calc(tmp, DispEta);           // P* * HT * Dxi-1
        Matrix tmp2 = Sub2.calc(getY(), getG());
        Matrix tmp3 = Multi.calc(tmp4,tmp2);     // P* * HT * Dxi-1 * (Y - G(X^))
        // если надо реализовать линейное соотношение, то в методе
        // getG() необходимо вернуть результат H*X^
        Xo = Add2.calc(tmp3,Xf);
        DList.add(tmp2);
        XoList.add(Xo);
    }

    protected  void getKo(){
        Matrix HT = Transpose.calc(H);
        Matrix tmp =  Multi.calc(HT,DispEta);
        MultiWith.calc(tmp,H);
        Matrix tmp2 = CInverse.calc(Kf);
        AddWith.calc(tmp,tmp2 );
        Ko = CInverse.calc(tmp);
        //Ko = GaussInv.calc(tmp);
        KoList.add(Ko);
    }

    protected  void getKf(){
        Matrix FT = Transpose.calc(F);
        Matrix tmp = Multi.calc(F,Ko);
        MultiWith.calc(tmp,FT);
        Kf = Add2.calc(tmp,DispXi);
        KfList.add(Kf);
    }

    protected  void getXf(){
        Xf = getFx(Xo,U);
        // если надо реализовать линейное соотношение, то в методе
        // getFx() необходимо вернуть результат F*Xo+U
        XfList.add(Xf);
    }


}
