package cn.lkllkllkl.configurableframelayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
 * 可对其中的DraggableImageView控件通过拖拽实现图片交换的FrameLayout.
 */

public class ConfigurableFrameLayout extends FrameLayout implements OnTriggerDragListener {

    private static final String TAG = ConfigurableFrameLayout.class.getSimpleName();

    private HashMap<View, RectF> mChildViewRects; // 保存所有子View的区域
    private ImageView mInterpolationImageView; // 拖动图片到另一控件时使用的中间ImageView

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

            // 设置子View所在的矩形区域
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

    private View mCurrentChildView; // 当前正在处理触摸事件的子View

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 事件是否被子View消费
        boolean handled = false;
        // 当前事件流若已被分发给某个子View处理，则将后续事件都分发给该子View
        if (mCurrentChildView != null) {
            handled = dispatchTouchEventToChild(event, mCurrentChildView);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // 获取位于触点的子View
                mCurrentChildView = viewInXY(event.getX(), event.getY());
                // 判断子View是否可以触发拖拽事件，可以的为其设置触发时的监听事件
                if (mCurrentChildView instanceof TriggerDraggable) {
                    ((TriggerDraggable) mCurrentChildView).setOnTriggerDragListener(this);
                }
                // 只在ACTION_DOWN时对事件进行分发，事件只能交由一个子View处理
                if (mCurrentChildView != null) {
                    handled =dispatchTouchEventToChild(event, mCurrentChildView);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 设置当前处理事件流的子View为null
                mCurrentChildView = null;

                break;
        }

        if (!handled) handled = super.onTouchEvent(event);
        return handled;
    }

    // 判断拖拽的ImageView是否已经设置了当前处理事件的子View的图片
    private boolean mInterpolationHasImg = false;

    /**
     * 拖拽ImageView,使之跟随触点位置移动
     * @param event 当前触摸事件
     */
    private void dragInterpolationImageView(MotionEvent event) {
        ImageView curImgView = null;
        if (mCurrentChildView instanceof ImageView) {
            curImgView = (ImageView) mCurrentChildView;
        }
        if (curImgView != null) {
            // 为中间控件设置图片
            if (!mInterpolationHasImg
                    && curImgView.getDrawable() instanceof BitmapDrawable) {
                Drawable drawable = curImgView.getDrawable();
                Bitmap bitmap = null;
                if (drawable instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                }
                if (bitmap != null) {
                    mInterpolationImageView.setImageBitmap(bitmap);
                }
                mInterpolationHasImg = true;
            }

            // 隐藏控件的图片
            curImgView.setImageAlpha(0);
            // 设置中间控件为可见
            mInterpolationImageView.setVisibility(VISIBLE);
            // 跟随手指移动中间控件
            LayoutParams lp = (LayoutParams) mInterpolationImageView.getLayoutParams();
            lp.setMargins((int)(event.getX() - mInterpolationImageView.getWidth() / 2),
                    (int)(event.getY() - mInterpolationImageView.getHeight() / 2),
                    0, 0);
            // 将中间控件移到最上层
            mInterpolationImageView.bringToFront();
        }
    }

    /**
     * 拖拽结束，通常为ACTION_UP时拖拽事件结束，调用该方法对一些标志位进行清理
     * 并设置各子控件的最终状态
     * @param event 当前触摸事件
     */
    private void dragFinish(MotionEvent event) {
        ImageView curImgView = null;
        if (mCurrentChildView instanceof ImageView) {
            curImgView = (ImageView) mCurrentChildView;
        }

        if (curImgView != null) {
            // 判断当前触点是否在其他子View内，若是则交换两者图片
            View v = viewInXY(event.getX(), event.getY());
            if (v instanceof ImageView) {
                exchangeImg(curImgView, (ImageView) v);
            }
            // 将之前拖拽过程隐藏当前处理事件的子View的图片显示出来
            curImgView.setImageAlpha(255);
            // 隐藏中间控件
            mInterpolationImageView.setVisibility(GONE);
            // 设置中间控件不含当前处理事件的子View的图片
            mInterpolationHasImg = false;
        }
    }

    /**
     * 交换两个ImageView的图片
     * @param fromImgView 源控件
     * @param toImgView 目标控件
     */
    private void exchangeImg(ImageView fromImgView, ImageView toImgView) {
        // 若源控件不包含图片则不交换
        if (toImgView == null || fromImgView == null || fromImgView.getDrawable() == null) {
            return;
        }

        Bitmap fromBmp = null;
        Bitmap toBmp = null;
        // 获取源控件图片
        if (fromImgView.getDrawable() instanceof BitmapDrawable) {
            fromBmp = ((BitmapDrawable) fromImgView.getDrawable()).getBitmap();
        }
        // 获取目标控件图片
        if (toImgView.getDrawable() instanceof BitmapDrawable) {
            toBmp = ((BitmapDrawable) toImgView.getDrawable()).getBitmap();
        }
        // 交换两者图片
        if (toBmp != null) fromImgView.setImageBitmap(toBmp);
        if (fromBmp != null) toImgView.setImageBitmap(fromBmp);
    }

    /**
     * 获取位于指定坐标的子View
     * @param x x坐标
     * @param y y坐标
     * @return 包含该坐标的子View，若找不到则返回null
     */
    @Nullable
    private View viewInXY(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            RectF rectF = mChildViewRects.get(child);
            if (rectF != null && rectF.contains(x, y)) {
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

    @Override
    public void onDrag(MotionEvent event) {
        // 由于传递过来的事件是相对于子View的坐标，所以需要进行变换
        final float offsetX = mCurrentChildView.getLeft() - getScrollX();
        final float offsetY = mCurrentChildView.getTop() - getScrollY();
        event.offsetLocation(offsetX, offsetY);

        dragInterpolationImageView(event);

        event.offsetLocation(-offsetX, -offsetY);
    }

    @Override
    public void onDragFinish(MotionEvent event) {
        // 由于传递过来的事件是相对于子View的坐标，所以需要进行变换
        final float offsetX = mCurrentChildView.getLeft() - getScrollX();
        final float offsetY = mCurrentChildView.getTop() - getScrollY();
        event.offsetLocation(offsetX, offsetY);

        dragFinish(event);

        event.offsetLocation(-offsetX, -offsetY);
    }
}
