package cn.lkllkllkl.configurableframelayout;

import android.view.MotionEvent;

/**
 * 实现该接口表示可触发拖拽事件
 */
public interface TriggerDraggable {
    // 判断是否触发拖拽事件
    boolean triggerDrag(MotionEvent event);
    // 设置拖拽事件监听器
    void setOnTriggerDragListener(OnTriggerDragListener listener);
}
