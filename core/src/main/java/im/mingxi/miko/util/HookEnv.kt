package im.mingxi.miko.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class HookEnv {
    public static Context hostContext;
    public static Activity hostActivity;
    public static Application hostApplication;
    public static ClassLoader hostClassLoader;
    public static ClassLoader moduleClassLoader;
}
