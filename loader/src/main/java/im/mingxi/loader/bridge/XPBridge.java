package im.mingxi.loader.bridge;

import java.lang.reflect.Member;

public abstract class XPBridge {
    private static XPBridge bridge;

    protected abstract boolean isApi100Impl();

    public static int getApiLevel() {
        return bridge.getApiLevelImpl();
    }

    protected abstract void hookAfterImpl(Member member, HookCallback hookCallback);

    protected abstract void hookBeforeImpl(Member member, HookCallback hookCallback);

    protected abstract Object invokeImpl(Member member, Object obj, Object[] objArr)
            throws Throwable;

    protected abstract void logImpl(String str);

    public static void setImpl(XPBridge xHooker) {
        bridge = xHooker;
    }

    public static boolean isApi100() {
        return bridge.isApi100Impl();
    }

    protected abstract int getApiLevelImpl();

    public static String getFrameworkName() {
        return bridge.getFrameworkNameImpl();
    }

    protected abstract String getFrameworkNameImpl();

    public static void hookBefore(Member member, HookCallback hookCallback) {
        bridge.hookBeforeImpl(member, hookCallback);
    }

    public static void hookAfter(Member member, HookCallback hookCallback) {
        bridge.hookAfterImpl(member, hookCallback);
    }

    /*
     * 这里有时间可以做个输出到文件或是Manager的开关，因为部分免root框架没有日志
     */
    public static void log(Object obj) {
        bridge.logImpl("[Miko]" + String.valueOf(obj));
    }

    public static Object invoke(Member member, Object obj, Object... objArr) throws Throwable {
        return bridge.invokeImpl(member, obj, objArr);
    }

    @FunctionalInterface
    public interface HookCallback {
        void onInvoke(HookParam hookParam) throws Throwable;
    }

    public abstract static class HookParam {
        public Object[] args;
        public Member method;
        public Object thisObject;

        public abstract Object getResult();

        public abstract Throwable getThrowable();

        public abstract void setResult(Object obj);

        public abstract void setThrowable(Throwable th);

        public abstract void unhook();

        public void resultNull() {
            setResult(null);
        }

        public void resultTrue() {
            setResult(true);
        }

        public void resultFalse() {
            setResult(false);
        }

        @Override
        public String toString() {
            return "HookParam[args="
                    + args
                    + ", method="
                    + method
                    + ", thisObject="
                    + thisObject
                    + "]";
        }
    }

}
