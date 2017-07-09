package net.orb15.yafvt.util;

public abstract class Usable {

    private volatile boolean inUse;

    protected Usable() {
        this.inUse = false;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
       this.inUse = inUse;
    }
}
