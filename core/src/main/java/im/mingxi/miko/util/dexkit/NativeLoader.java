package im.mingxi.miko.util.dexkit;

import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import im.mingxi.core.BuildConfig;
import im.mingxi.loader.bridge.XPBridge;
import im.mingxi.loader.hotpatch.HotPatch;
import im.mingxi.loader.util.DataUtil;
import im.mingxi.loader.util.FileUtil;
import im.mingxi.loader.util.PathUtil;
import im.mingxi.miko.util.HookEnv;

public abstract class NativeLoader {
    private NativeLoader() {
        throw new RuntimeException("No instance!");
    }

    public static void loadLibrary(@NonNull String name) {
        String cachePath =
                HookEnv.hostContext.getCacheDir()
                        + "/"
                        + DataUtil.getStrMD5(Settings.Secure.ANDROID_ID + "_").substring(0, 8)
                        + "/";
        String tempName = "" + name.hashCode();
        FileUtil.deleteFile(new File(cachePath + tempName));
        outputLibToCache(cachePath + tempName, name);
        System.load(cachePath + tempName);
    }

    private static void outputLibToCache(String cachePath, String name) {
        String apkPath = BuildConfig.DEBUG ? PathUtil.moduleApkPath : HotPatch.INSTANCE.getHotPatchAPKPath();

        try {
            ZipInputStream zInp = new ZipInputStream(new FileInputStream(apkPath));
            ZipEntry entry;
            while ((entry = zInp.getNextEntry()) != null) {
                if (android.os.Process.is64Bit()
                        && entry.getName().startsWith("lib/arm64-v8a/" + name)) {
                    FileUtil.writeToFile(cachePath, DataUtil.readAllBytes(zInp));
                    break;
                } else if (!android.os.Process.is64Bit()
                        && entry.getName().startsWith("lib/armeabi-v7a/" + name)) {
                    FileUtil.writeToFile(cachePath, DataUtil.readAllBytes(zInp));
                    break;
                } else if (entry.getName().startsWith("assets/native/" + name)) {
                    FileUtil.writeToFile(cachePath, DataUtil.readAllBytes(zInp));
                    break;
                }
            }
        } catch (Exception ignored) {
            XPBridge.log(Log.getStackTraceString(ignored));
        }
    }
}
