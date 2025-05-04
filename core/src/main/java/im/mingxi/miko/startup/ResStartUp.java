package im.mingxi.miko.startup;
import android.content.Context;
import im.mingxi.loader.hotpatch.HotPatch;
import im.mingxi.miko.startup.util.XRes;

public class ResStartUp {
    public static void doLoad(Context ctx){
        XRes.addAssetsPath(ctx,HotPatch.hotPatchAPKPath);
    }
}
