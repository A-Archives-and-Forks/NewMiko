package im.mingxi.loader.hotpatch;

import android.util.Log;
import dalvik.system.DexClassLoader;
import im.mingxi.loader.bridge.XPBridge;
import im.mingxi.loader.util.ActivityUtil;
import im.mingxi.loader.util.FileUtil;
import im.mingxi.loader.util.HttpUtil;
import im.mingxi.loader.util.PathUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class HotPatch {
    public static final String hotPatchPath = PathUtil.dataPath + "HotPatch/";
    public static final String hotPatchAPKPath = hotPatchPath + "release.apk";

    public static boolean onLoad() {
        String versions =
                HttpUtil.sendDataRequest("http://miao.yuexinya.top/HotPatch/versions.txt");
        if (versions != null && !"".equals(versions)) { // 正常连接到服务器
            if (getSign().equals(versions)) { // 签名一致，加载模块
                doLoad();
            } else { // 签名不一致，下载新版本
                try {
                    Path path = Path.of(hotPatchAPKPath);
                    File file = path.toFile();
                    if (!file.exists()) file.createNewFile();
                    HttpUtil.downloadToFile(
                            "http://miao.yuexinya.top/HotPatch/release.apk", hotPatchAPKPath);
                    /*懒得获取Context来调用 {@link im.mingxi.loader.util.ActivityUtil#killAppProcess(Context)}，所以我们暴力点*/
                    System.exit(0);
                } catch (Exception err) {
                    XPBridge.log(Log.getStackTraceString(err));
                }
            }
            return true;
        }
        return false;
    }

    private static void doLoad() {
        DexClassLoader dexClassLoader =
                new DexClassLoader(
                        hotPatchAPKPath,
                        hotPatchPath + "Optimized",
                        null,
                        HotPatch.class.getClassLoader());
        try {
            Class startupClass = dexClassLoader.loadClass("im.mingxi.miko.startup.StartUp");
            Method initMet = startupClass.getDeclaredMethod("doLoad");
            initMet.setAccessible(true);
            initMet.invoke(null);
        } catch (Exception err) {
            XPBridge.log(Log.getStackTraceString(err));
        }
    }


    private static String getSign() {
        return "(version = " + 666 + ")";
    }
}
