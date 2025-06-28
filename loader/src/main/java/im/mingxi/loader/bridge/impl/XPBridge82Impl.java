package im.mingxi.loader.bridge.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.IXUnhook;
import im.mingxi.loader.bridge.XPBridge;

public class XPBridge82Impl extends XPBridge {

    @Override
    protected void hookAfterImpl(Member member, HookCallback hookCallback) {
        if (!(member instanceof Method) && !(member instanceof Constructor))
            throw new RuntimeException("Cannot hook:" + member);
        AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(
                XposedBridge.hookMethod(
                        member,
                        new XC_MethodHook(50) {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                hookCallback.onInvoke(
                                        new XP82HookParam((IXUnhook) atomicReference.get(), param));
                            }
                        }));
    }

    @Override
    protected void hookBeforeImpl(Member member, HookCallback hookCallback) {
        if (!(member instanceof Method) && !(member instanceof Constructor))
            throw new RuntimeException("Cannot hook:" + member);
        AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(
                XposedBridge.hookMethod(
                        member,
                        new XC_MethodHook(50) {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                hookCallback.onInvoke(
                                        new XP82HookParam((IXUnhook) atomicReference.get(), param));
                            }
                        }));
    }

    @Override
    protected Object invokeImpl(Member member, Object obj, Object... objArr) throws Throwable {
        return XposedBridge.invokeOriginalMethod(member, obj, objArr);
    }

    @Override
    protected void logImpl(String str) {
        XposedBridge.log(str);
    }

    public class XP82HookParam extends HookParam {

        private XC_MethodHook.MethodHookParam methodHookParam;
        private IXUnhook unHook;

        public XP82HookParam(IXUnhook hook, XC_MethodHook.MethodHookParam param) {
            this.methodHookParam = param;
            this.unHook = hook;
            this.args = param.args;
            this.method = param.method;
            this.thisObject = param.thisObject;
        }

        @Override
        public Object getResult() {
            return methodHookParam.getResult();
        }

        @Override
        public Throwable getThrowable() {
            return methodHookParam.getThrowable();
        }

        @Override
        public void setResult(Object obj) {
            methodHookParam.setResult(obj);
        }

        @Override
        public void setThrowable(Throwable th) {
            methodHookParam.setThrowable(th);
        }

        @Override
        public void unhook() {
            unHook.unhook();
        }
    }

    @Override
    protected boolean isApi100Impl() {
        return false;
    }

    @Override
    protected int getApiLevelImpl() {
        return XposedBridge.getXposedVersion();
    }
}
