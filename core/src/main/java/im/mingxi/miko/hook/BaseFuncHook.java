package im.mingxi.miko.hook;
import java.util.ArrayList;

public abstract class BaseFuncHook {
    public final String KEY = this.getClass().getName();
    public final ArrayList<Throwable> mErrors = new ArrayList<>();
    
    public abstract boolean initOnce() throws Throwable;
    
    
}
