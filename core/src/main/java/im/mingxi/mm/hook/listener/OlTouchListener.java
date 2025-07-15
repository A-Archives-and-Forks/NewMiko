package im.mingxi.mm.hook.listener;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import im.mingxi.core.R;
import im.mingxi.loader.bridge.XPBridge;
import im.mingxi.miko.util.Reflex;
import im.mingxi.miko.util.xpcompat.XPHelpers;
import im.mingxi.mm.hook.MsgLeftSileHook;

public class OlTouchListener implements View.OnTouchListener {

    int SWIPE_STATE_KEY = R.id.xposed_tag_key_for_swipe;

    int SWIPE_ACTION_THRESHOLD = -50;

    Object Q9 = null;
    ClassLoader classLoader = null;

    public OlTouchListener(Object q9, ClassLoader classLoader) {
        this.Q9 = q9;
        this.classLoader = classLoader;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isMultiSelectMode = false;
        try {
            isMultiSelectMode = (Boolean) Reflex.findFieldObj(v)
                    .setReturnType(Boolean.TYPE).get().get(v);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //XPHelpers.getBooleanField(v, "d");
        if (isMultiSelectMode) {
            return false;
        }

        SwipeState state = (SwipeState) v.getTag(SWIPE_STATE_KEY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.animate().cancel();
                state = new SwipeState();


                state.initialRawX = event.getRawX();

                state.initialY = event.getY();
                state.touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
                v.setTag(SWIPE_STATE_KEY, state);
                return true;

            case MotionEvent.ACTION_MOVE:

                if (state == null) return false;


                float deltaX = event.getRawX() - state.initialRawX;
                float deltaY = event.getY() - state.initialY;

                if (!state.isDragging && Math.abs(deltaX) > state.touchSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
                    state.isDragging = true;
                    if (v.getParent() != null) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                if (state.isDragging) {
                    v.setTranslationX(Math.min(0f, deltaX));
                }
                return state.isDragging;


            case MotionEvent.ACTION_UP: {

                if (state == null) return false;


                if (state.isDragging) {
                    float finalDeltaX = event.getRawX() - state.initialRawX;

                    if (finalDeltaX < SWIPE_ACTION_THRESHOLD) {

                        //  Toast.makeText(v.getContext(), "左滑动作触发!", Toast.LENGTH_SHORT).show();
                        // XPBridge.log("左滑动作触发! 距离: " + finalDeltaX);
                        // if (MsgLeftSileHook.chatFooter != null && Q9 != null && classLoader != null) {
                       //      XPHelpers.callMethod(MsgLeftSileHook.chatFooter, "A", Q9);
                        // } else {
                        //     XPBridge.log("存在参数为Null，左滑逻辑不执行");
                        // }
                    }

                    v.animate().translationX(0f).setDuration(200).start();

                    v.setTag(SWIPE_STATE_KEY, null);

                    return true;
                }


                v.setTag(SWIPE_STATE_KEY, null);
                return false;

            }

            case MotionEvent.ACTION_CANCEL: {

                if (state == null) return false;


                if (state.isDragging) {

                    v.animate().translationX(0f).setDuration(200).start();
                }

                v.setTag(SWIPE_STATE_KEY, null);

                return true;
            }
        }
        return false;
    }
}
