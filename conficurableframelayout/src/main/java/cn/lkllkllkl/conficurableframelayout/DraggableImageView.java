package cn.lkllkllkl.conficurableframelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import cn.lkllkllkl.transformativeimageview.TransformativeImageView;

/**
 * 可拖拽ImageView
 */

public class DraggableImageView extends TransformativeImageView{
    private static final String TAG = DraggableImageView.class.getSimpleName();
    private static final int DEFAULT_TRIGGER_DISTANCE = 100; // 默认触发拖拽的距离为100px

    public static final int LEFT_BOUNDARY = 0; // 左边界
    public static final int TOP_BOUNDARY = 1; // 上边界
    public static final int RIGHT_BOUNDARY = 2; // 右边界
    public static final int BOTTOM_BOUNDARY = 3; // 下边界

    private int mBoundary = BOTTOM_BOUNDARY; // 触发拖拉事件边界
    private float mTriggerDistance = DEFAULT_TRIGGER_DISTANCE; // 触发拖拉事件的距离

    public DraggableImageView(Context context) {
        this(context, null);
    }

    public DraggableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraggableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainAttrs(context, attrs);
    }

    private void obtainAttrs(Context context, AttributeSet attributes) {
        if (attributes == null) return;
        TypedArray typedArray =
                context.obtainStyledAttributes(attributes, R.styleable.DraggableImageView);

        mBoundary = typedArray.getInt(
                R.styleable.DraggableImageView_boundary, BOTTOM_BOUNDARY);
        mTriggerDistance = typedArray.getDimension(
                R.styleable.DraggableImageView_trigger_distance, DEFAULT_TRIGGER_DISTANCE);

        typedArray.recycle();
    }

    /**
     * 获取触发拖拽事件的边界
     *
     * {@link #LEFT_BOUNDARY}
     * {@link #TOP_BOUNDARY}
     * {@link #RIGHT_BOUNDARY}
     * {@link #BOTTOM_BOUNDARY}
     *
     * @return 边界方向
     */
    public int getBoundary() {
        return mBoundary;
    }

    /**
     * 获取触发拖拽事件距离
     * @return 触发拖拽事件时超过指定边界的距离
     */
    public float getTriggerDistance() {
        return mTriggerDistance;
    }
}
