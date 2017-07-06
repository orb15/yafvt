package net.orb15.yafvt.util;

public class Pair<T, R> {

    private T t;
    private R r;

    public Pair() {}

    public Pair(T t, R r) {
        this.t = t;
        this.r = r;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public R getR() {
        return r;
    }

    public void setR(R r) {
        this.r = r;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "t=" + t +
                ", r=" + r +
                '}';
    }


}
