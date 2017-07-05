package net.orb15.yafvt.util;

public class YafvtException extends RuntimeException {

    public YafvtException(String format, Object... args ) {
        super(String.format(format, args));
    }

    public YafvtException(String msg) {
        super(msg);
    }

    public YafvtException(Exception e,String format, Object... args ) {
        super(String.format(format, args), e);
    }

    public YafvtException(Exception e, String msg) {
        super(msg, e);
    }

}
