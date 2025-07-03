package im.mingxi.mm.hook.listener;

public class SwipeState {
    // float initialX; // 不再需要相对坐标
    public float lastRawX;

    public float initialRawY;
    public float initialRawX;
    public float initialY;
    public boolean isDragging = false;
    public int touchSlop;

    public SwipeState() {

    }
}
