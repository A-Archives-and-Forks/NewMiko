package im.mingxi.miko.startup;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import com.tencent.mmkv.MMKV;
import im.mingxi.loader.bridge.XPBridge;
import static im.mingxi.loader.util.Constants.QQ_PACKAGENAME;
import static im.mingxi.loader.util.Constants.WECHAT_PACKAGENAME;
import im.mingxi.loader.XposedPackage;
import im.mingxi.miko.startup.util.XRes;
import im.mingxi.miko.util.HookEnv;
import im.mingxi.miko.util.Reflex;
import im.mingxi.miko.util.dexkit.NativeLoader;
import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public class StartUp {
    private static final AtomicBoolean isMMKVInit = new AtomicBoolean();
    private static final AtomicBoolean isActInit = new AtomicBoolean();

    /*
     * 反射调用此方法启动核心模块
     */
    public static void doLoad() {
        HookEnv.moduleClassLoader = StartUp.class.getClassLoader();

        Reflex.setHostClassLoader(XposedPackage.classLoader);
        Class<?> appClass = null;
        switch (XposedPackage.packageName) {
            case QQ_PACKAGENAME -> {
                appClass = Reflex.loadClass("com.tencent.mobileqq.qfix.QFixApplication");
            }
            case WECHAT_PACKAGENAME -> {
                appClass = Reflex.loadClass("com.tencent.mm.app.Application");
            }
                /*case TIM_PACKAGENAME -> {}
                case BILI_PACKAGENAME -> {}*/
            default -> {
                try {
                    appClass = Class.forName("android.app.Application");
                } catch (ClassNotFoundException err) {
                }
            }
        }

        if (appClass == null) {
            try {
                appClass = Class.forName("android.app.Application");
            } catch (ClassNotFoundException unused) {
                // 这理论上是不该出现的错误
            }
        }
        // hook android.app.Application.attachBaseContext
        XPBridge.hookAfter(
                Reflex.findMethod(appClass).setMethodName("attachBaseContext").get(),
                param -> {
                    Context context = (Context) param.args[0];
                    HookEnv.hostContext = context;
                    HookEnv.hostApplication = (Application) param.thisObject;
                    ResStartUp.doLoad(context); // 重复注入资源防止部分免root框架注入资源异常
                    if (!isMMKVInit.getAndSet(true)) initializeMMKV(context);
                });
        /*
         * To prevent the framework from passing the wrong class loader,
         *  we use {@link #getClassLoader()} to get the class loader.
         */
        XPBridge.hookAfter(
                Reflex.findMethod(Activity.class).setMethodName("onResume").get(),
                param -> {
                    Activity activity = (Activity) param.thisObject;
                    HookEnv.hostActivity = activity;
                    ResStartUp.doLoad(activity); // 重复注入资源防止部分免root框架注入资源异常
                    if (!isActInit.getAndSet(true)) {
                        ClassLoader xLoader = activity.getClassLoader();
                        if (xLoader != null) {
                            HookEnv.hostClassLoader = xLoader;
                            XPBridge.log("Load Successful!");
                        }
                    }
                });
        Method moduleLoadMeth = null;
        try {
            moduleLoadMeth =
                    Class.forName("im.mingxi.miko.MainActivity").getDeclaredMethod("onCreate",Bundle.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // 理论不应该出现的错误
            throw new RuntimeException("Do not find MainActivity class or onCreate Method"+e);
        }
        XPBridge.hookAfter(moduleLoadMeth,param->{
            try {
            	param.thisObject.getClass().getDeclaredMethod("onModuleLoad").invoke(param.thisObject);
            } catch(Exception ignored) {
            	//理论不应该出现的错误
            }
        });
    }

    private static void initializeMMKV(Context ctx) {
        NativeLoader.loadLibrary("libmmkv.so");
        File dataDir = ctx.getDataDir();
        File filesDir = ctx.getFilesDir();
        File mmkvDir = new File(filesDir, "Miko_MMKV");
        if (!mmkvDir.exists()) {
            mmkvDir.mkdirs();
        }
        // MMKV requires a ".tmp" cache directory, we have to create it manually
        File cacheDir = new File(mmkvDir, ".tmp");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        MMKV.initialize(ctx, mmkvDir.getAbsolutePath());
        MMKV.mmkvWithID("global_config", MMKV.MULTI_PROCESS_MODE);
        MMKV.mmkvWithID("global_cache", MMKV.MULTI_PROCESS_MODE);
    }
}
