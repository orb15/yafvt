package net.orb15.yafvt.util;

public class Pair<M, N> {

    private M m;
    private N n;

    public Pair() {}

    public Pair(M m, N n) {
        this.m = m;
        this.n = n;
    }

    public M getM() {
        return m;
    }

    public void setM(M m) {
        this.m = m;
    }

    public N getN() {
        return n;
    }

    public void setN(N n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "m=" + m +
                ", n=" + n +
                '}';
    }


}
