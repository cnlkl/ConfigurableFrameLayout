package cn.lkllkllkl.conficurableframelayout;

import android.view.MotionEvent;

/**
 * 拖拽事件监听器
 */

public interface OnTriggerDragListener {
    // 在拖拽的时候调用
    void onDrag(MotionEvent event);
    // 拖拽事件结束时调用
    void onDragFinish(MotionEvent event);
}
