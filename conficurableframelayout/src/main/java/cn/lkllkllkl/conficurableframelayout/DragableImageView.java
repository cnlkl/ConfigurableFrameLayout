package cn.lkllkllkl.conficurableframelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.lkllkllkl.transformativeimageview.TransformativeImageView;

/**
 * 可拖拉ImageView
 */

public class DragableImageView extends TransformativeImageView{
    private static final String TAG = DragableImageView.class.getSimpleName();
    private static final int DEFAULT_TRIGGER_DISTANCE = 100; // 默认触发拖拉的距离为100px

    private static final int LEFT_BOUNDARY = 0; // 左边界
    private static final int TOP_BOUNDARY = 1; // 上边界
    private static final int RIGHT_BOUNDARY = 2; // 右边界
    private static final int BOTTOM_BOUNDARY = 3; // 下边界

    private int mBoundary = BOTTOM_BOUNDARY; // 触发拖拉事件边界
    private float mTriggerDistance = DEFAULT_TRIGGER_DISTANCE; // 触发拖拉事件的距离

    public DragableImageView(Context context) {
        this(context, null);
    }

    public DragableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttrs(context, attrs);
    }

    private void obtainAttrs(Context context, AttributeSet attributes) {
        if (attributes == null) return;
        TypedArray typedArray =
                context.obtainStyledAttributes(attributes, R.styleable.DragableImageView);

        mBoundary = typedArray.getInt(R.styleable.DragableImageView_boundary, LEFT_BOUNDARY);
        mTriggerDistance =
                typedArray.getDimension(R.styleable.DragableImageView_trigger_distance, 100);

        typedArray.recycle();
    }

    /**
     * 根据当前触摸事件超出所设置边界的距离,判断是否触发拖拉事件
     * @param event 当前触摸事件
     * @return 触发则返回true, 否则返回false
     */
    public boolean triggerDrag(MotionEvent event) {
        boolean canDrag = false;
        switch (mBoundary) {
            case LEFT_BOUNDARY:
                if (getLeft() - event.getX() > mTriggerDistance) canDrag = true;
                break;
            case TOP_BOUNDARY:
                if (getTop() - event.getY() > mTriggerDistance) canDrag = true;
                break;
            case RIGHT_BOUNDARY:
                if (event.getX() - getRight() > mTriggerDistance) canDrag = true;
                break;
            case BOTTOM_BOUNDARY:
                if (event.getY() - getBottom() > mTriggerDistance) canDrag = true;
                break;
        }
        return canDrag;
    }
}
