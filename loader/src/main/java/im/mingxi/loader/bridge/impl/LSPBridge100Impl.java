package im.mingxi.loader.bridge.impl;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import im.mingxi.loader.bridge.XPBridge;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.annotations.AfterInvocation;
import io.github.libxposed.api.annotations.BeforeInvocation;
import io.github.libxposed.api.annotations.XposedHooker;

public class LSPBridge100Impl extends XPBridge {
    private XposedInterface sApi;
    private static final ConcurrentHashMap<Member, ArrayList<XPBridge.HookCallback>>
            beforeCallbacks = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Member, ArrayList<XPBridge.HookCallback>> afterCallbacks =
            new ConcurrentHashMap<>();

    public LSPBridge100Impl(XposedInterface xposedInterface) {
        this.sApi = xposedInterface;
    }

    @Override
    protected void hookAfterImpl(Member member, HookCallback hookCallback) {
        if (member instanceof Method) this.sApi.hook((Method) member, 50, LSP100Hooker.class);
        else this.sApi.hook((Constructor) member, 50, LSP100Hooker.class);
        ArrayList<XPBridge.HookCallback> list =
                afterCallbacks.computeIfAbsent(
                        member,
                        key -> {
                            return new ArrayList<>();
                        });
        list.add(hookCallback);
    }

    @Override
    protected void hookBeforeImpl(Member member, HookCallback hookCallback) {
        if (member instanceof Method) this.sApi.hook((Method) member, 50, LSP100Hooker.class);
        else this.sApi.hook((Constructor) member, 50, LSP100Hooker.class);
        ArrayList<XPBridge.HookCallback> list =
                beforeCallbacks.computeIfAbsent(
                        member,
                        key -> {
                            return new ArrayList<>();
                        });
        list.add(hookCallback);
    }

    @Override
    protected Object invokeImpl(Member member, Object obj, Object... objArr) throws Throwable {
        if (member instanceof Method) {
            return this.sApi.invokeOrigin((Method) member, obj, objArr);
        }
        throw new IllegalArgumentException("target must be Method");
    }

    @Override
    protected void logImpl(String str) {
        this.sApi.log(str);
    }

    @XposedHooker
    public static class LSP100Hooker implements XposedInterface.Hooker {
        @BeforeInvocation
        public static void before(XposedInterface.BeforeHookCallback beforeHookCallback) {
            ArrayList<XPBridge.HookCallback> list =
                    LSPBridge100Impl.beforeCallbacks.get(beforeHookCallback.getMember());
            if (list != null) {
                for (XPBridge.HookCallback callBack : list) {
                    LSP100HookParam param =
                            new LSP100HookParam(beforeHookCallback, null, list, callBack);
                    param.args = beforeHookCallback.getArgs();
                    param.method = beforeHookCallback.getMember();
                    param.thisObject = beforeHookCallback.getThisObject();
                    try {
                        callBack.onInvoke(param);
                    } catch (Throwable th) {
                        XPBridge.log(Log.getStackTraceString(th));
                    }
                }
            }
        }

        @AfterInvocation
        public static void after(XposedInterface.AfterHookCallback afterHookCallback) {
            ArrayList<XPBridge.HookCallback> list =
                    LSPBridge100Impl.afterCallbacks.get(afterHookCallback.getMember());
            if (list != null) {
                for (XPBridge.HookCallback callBack : list) {
                    LSP100HookParam param =
                            new LSP100HookParam(null, afterHookCallback, list, callBack);
                    param.args = afterHookCallback.getArgs();
                    param.method = afterHookCallback.getMember();
                    param.thisObject = afterHookCallback.getThisObject();
                    try {
                        callBack.onInvoke(param);
                    } catch (Throwable th) {
                        XPBridge.log(Log.getStackTraceString(th));
                    }
                }
            }
        }
    }

    public static class LSP100HookParam extends HookParam {

        private XposedInterface.BeforeHookCallback before;
        private XposedInterface.AfterHookCallback after;
        private ArrayList callBacks;
        public XPBridge.HookCallback callBack;

        public LSP100HookParam(
                XposedInterface.BeforeHookCallback sBefore,
                XposedInterface.AfterHookCallback sAfter,
                ArrayList sCallBacks,
                XPBridge.HookCallback sCallBack) {
            this.before = sBefore;
            this.after = sAfter;
            this.callBack = sCallBack;
            this.callBacks = sCallBacks;
        }

        @Override
        public Object getResult() {
            return after.getResult();
        }

        @Override
        public Throwable getThrowable() {
            return after.getThrowable();
        }

        @Override
        public void setResult(Object obj) {
            if (before != null) before.returnAndSkip(obj);
            if (after != null) after.setResult(obj);
        }

        @Override
        public void setThrowable(Throwable th) {
            if (before != null) before.throwAndSkip(th);
            if (after != null) after.setThrowable(th);
        }

        @Override
        public void unhook() {
            callBacks.remove(callBack);
        }
    }

    @Override
    protected boolean isApi100Impl() {
        return true;
    }

    @Override
    protected int getApiLevelImpl() {
        return 100;
    }

    @Override
    protected String getFrameworkNameImpl() {
        return sApi.getFrameworkName();
    }
}
