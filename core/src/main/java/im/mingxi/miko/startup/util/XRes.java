package im.mingxi.miko.startup.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import im.mingxi.loader.util.PathUtil;
import java.lang.reflect.Method;

public abstract class XRes {

  public static void addAssetsPath(Context context) {
    addAssetsPath(context.getResources(), PathUtil.moduleApkPath);
  }
    
    public static void addAssetsPath(Context context, String path) {
    addAssetsPath(context.getResources(), path);
  }

  @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
  public static void addAssetsPath(Resources resources, String str) {
    try {
      Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
      method.setAccessible(true);
      method.invoke(resources.getAssets(), str);
    } catch (Exception unused) {
    }
  }
}
