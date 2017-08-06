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

    /**
     * 可触发拖拽事件的边界,若数组某个index的变量为true，则表示该index对应的边界可以触发拖拽事件；
     * 默认所有边界均不可触发拖拽事件
     * index: {0, 1, 2, 3} -> boundary: {left, top, right, bottom}
     *
     * 例：mBoundary = {true, false, false, true} 表示左边界与下边界可触发拖拽事件
     */
    private boolean[] mBoundary = new boolean[4];
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

        mBoundary[0] = typedArray.getBoolean(
                R.styleable.DraggableImageView_boundary_left, false);
        mBoundary[1] = typedArray.getBoolean(
                R.styleable.DraggableImageView_boundary_top, false);
        mBoundary[2] = typedArray.getBoolean(
                R.styleable.DraggableImageView_boundary_right, false);
        mBoundary[3] = typedArray.getBoolean(
                R.styleable.DraggableImageView_boundary_bottom, false);
        mTriggerDistance = typedArray.getDimension(
                R.styleable.DraggableImageView_trigger_distance, DEFAULT_TRIGGER_DISTANCE);

        typedArray.recycle();
    }

    /**
     * 可触发拖拽事件的边界,若数组某个index的变量为true，则表示该index对应的边界可以触发拖拽事件；
     * index: {0, 1, 2, 3} -> boundary: {left, top, right, bottom}
     *
     * 例：{true, false, false, true} 表示左边界与下边界可触发拖拽事件
     *
     * @return 表示每个边界是否可触发拖拽事件的数组
     */
    public boolean[] getBoundary() {
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
