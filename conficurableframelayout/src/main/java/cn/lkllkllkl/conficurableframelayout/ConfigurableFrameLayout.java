package cn.lkllkllkl.conficurableframelayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * 可配置的FrameLayout.
 */

public class ConfigurableFrameLayout extends FrameLayout {

    private static final String TAG = ConfigurableFrameLayout.class.getSimpleName();

    private HashMap<View, RectF> mChildViewRects; // 保存所有子View的区域
    private ImageView mInterpolationImageView; // 拖动图片到另一控时使用的中间View


    public ConfigurableFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public ConfigurableFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfigurableFrameLayout(@NonNull Context context,
                                   @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initInterpolationView();
    }

    private void init() {
        mChildViewRects = new HashMap<>();
    }

    private void initInterpolationView() {
        mInterpolationImageView = new ImageView(getContext());
        // TODO dp转px, 大小，透明度为可配置
        LayoutParams lp = new LayoutParams(300, 300);
        mInterpolationImageView.setLayoutParams(lp);
        mInterpolationImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mInterpolationImageView.setAlpha(0.5f);
        mInterpolationImageView.setVisibility(GONE);
        addView(mInterpolationImageView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initChildViewRect();
    }

    /**
     * 获取各子View所在区域
     */
    private void initChildViewRect() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            // 避免多次创建对象
            RectF rect = mChildViewRects.get(child);
            if (rect == null) {
                rect = new RectF();
            }

            rect.set(lp.leftMargin, lp.topMargin,
                    lp.leftMargin + child.getWidth(), lp.topMargin + child.getHeight());

            mChildViewRects.put(child, rect);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        // 拦截所有事件
        return true;
    }

    private View mCurrentChildView; // 当前正在处理事件的子View
    private boolean mCanDrag = false; // 是否可以将图片拖拉出控件

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false; // 事件是否被消费
        // 不处于图片可拖拉的状态，且当前事件流若已被分发给某个子View处理，则将后续事件都分发给该子View
        if (mCurrentChildView != null && !mCanDrag) {
            handled = dispatchTouchEventToChild(event, mCurrentChildView);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // 只在ACTION_DOWN时对事件进行分发，事件只能交由一个子View处理
                mCurrentChildView = viewInXY(event.getX(), event.getY());
                if (mCurrentChildView != null) {
                    handled =dispatchTouchEventToChild(event, mCurrentChildView);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                // 只有当不可拖拉的时候才判断是否触发，否则会导致无法将图片拖拉到设定的边界之前的区域
                if (!mCanDrag && triggerDrag(event)) {
                    mCanDrag = true;
                }
                dragInterpolationImageView(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 拖拉结束，处理后续任务
                onDragFinish(event);
                // 设置当前处理事件流的子View为null
                mCurrentChildView = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }

        if (!handled) handled = super.onTouchEvent(event);
        return handled;
    }

    /**
     * 判断是否触发拖拉事件
     * @param event 触摸事件
     * @return 符合触发条件则返回true，否则返回false
     */
    private boolean triggerDrag(MotionEvent event) {
        if (mCurrentChildView instanceof DragableImageView) {
            return ((DragableImageView) mCurrentChildView).triggerDrag(event);
        }
        return false;
    }

    // 判断当前中间控件是否已经设置了当前处理事件的控件的图片
    private boolean mInterpolationHasImg = false;
    private void dragInterpolationImageView(MotionEvent event) {
        ImageView curImgView = null;
        if (mCurrentChildView instanceof ImageView) {
            curImgView = (ImageView) mCurrentChildView;
        }
        if (mCanDrag && curImgView != null) {
            // 为中间控件设置图片
            if (!mInterpolationHasImg
                    && curImgView.getDrawable() instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) curImgView.getDrawable()).getBitmap();
                if (bitmap != null) mInterpolationImageView.setImageBitmap(bitmap);
                mInterpolationHasImg = true;
            }

            // 隐藏控件的图片
            curImgView.setImageAlpha(0);
            // 将中间控件移到最上层
            mInterpolationImageView.bringToFront();
            // 设置中间控件为可见
            mInterpolationImageView.setVisibility(VISIBLE);
            // 跟随手指移动中间控件
            LayoutParams lp = (LayoutParams) mInterpolationImageView.getLayoutParams();
            lp.setMargins((int)(event.getX() - mInterpolationImageView.getWidth() / 2),
                    (int)(event.getY() - mInterpolationImageView.getHeight() / 2),
                    0, 0);
        }
    }

    /**
     * 拖动结束，通常为ACTION_UP时，调用该方法，对一些标志位进行清理
     * 并设置各子控件的最终状态
     * @param event
     */
    private void onDragFinish(MotionEvent event) {
        ImageView curImgView = null;
        if (mCurrentChildView instanceof ImageView) curImgView = (ImageView) mCurrentChildView;

        if (mCanDrag && curImgView != null) {
            // 获取拖拉到的目标控件
            View v = viewInXY(event.getX(), event.getY());
            if (v instanceof ImageView) {
                exchangeImg(curImgView, (ImageView) v);
            }
            curImgView.setImageAlpha(255);
            mInterpolationImageView.setVisibility(GONE);
            mInterpolationHasImg = false;
            mCanDrag = false;
        }
    }

    /**
     * 交换两个ImageView的图片
     * @param fromImgView 源控件
     * @param toImgView 目标控件
     */
    private void exchangeImg(ImageView fromImgView, ImageView toImgView) {
        if (toImgView == null || fromImgView == null || fromImgView.getDrawable() == null) {
            return;
        }

        Bitmap fromBmp = null;
        Bitmap toBmp = null;
        if (fromImgView.getDrawable() instanceof BitmapDrawable) {
            fromBmp = ((BitmapDrawable) fromImgView.getDrawable()).getBitmap();
        }
        if (toImgView.getDrawable() instanceof BitmapDrawable) {
            toBmp = ((BitmapDrawable) toImgView.getDrawable()).getBitmap();
        }
        if (toBmp != null) fromImgView.setImageBitmap(toBmp);
        if (fromBmp != null) toImgView.setImageBitmap(fromBmp);
    }

    /**
     * 获取位于当前坐标的子View
     * @param x x坐标
     * @param y y坐标
     * @return 包含该坐标的子View，若找不到则返回null
     */
    @Nullable
    private View viewInXY(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (mChildViewRects.get(child).contains(x, y)) {
                return child;
            }
        }
        return null;
    }

    /**
     * 将触摸事件坐标变换后传递给子View
     * @return true如果事件被子View消费，否则返回false
     */
    private boolean dispatchTouchEventToChild(MotionEvent event, View child) {
        final float offsetX = getScrollX() - child.getLeft();
        final float offsetY = getScrollY() - child.getTop();
        event.offsetLocation(offsetX, offsetY);

        boolean handled = child.dispatchTouchEvent(event);

        event.offsetLocation(-offsetX, -offsetY);
        return handled;
    }
}
