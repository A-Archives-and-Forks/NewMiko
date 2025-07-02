package im.mingxi.miko.util;

public class JniBridge {
    static {
        System.loadLibrary("miko");
    }

    public static native boolean cmd(String cmd);
}
