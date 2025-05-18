package im.mingxi.miko.startup;

import static im.mingxi.miko.startup.util.XRes.*;
import android.content.Context;
import im.mingxi.loader.hotpatch.HotPatch;

public class ResStartUp {
    public static void doLoad(Context ctx){
        addAssetsPath(ctx, HotPatch.INSTANCE.getHotPatchAPKPath());
    }
}
