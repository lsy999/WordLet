package com.wjc.worldlet.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;
import com.wjc.worldlet.activity.MainActivity;
import com.wjc.worldlet.application.SoftApplication;
import com.wjc.worldlet.tabhost.TabFragment;


/**
 * Created by ${万嘉诚} on 2016/10/1.
 * 微信：wjc398556712
 * 作用：自定义侧面菜单布
 *
 * 1、创建类 DragLayout继承FrameLayout
 * 2、创建 MainActivity
 * 3、在布局文件activity_main中把DragLayout作为容器
 * 4、初始化ViewDragHelper    mDragHelper = ViewDragHelper.create(this, mCallBack);
 * 5、把事件传递给ViewDragHelper
 * 6、初始化子控件并得到屏幕的宽高信息
 * 7、实现回调方法 ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {}
 * 8、实现打开关闭时动画的平滑移动
 * 9、实现动画效果   animViews(float percent)
 *    为了能够实现缩放、透明度、平移的动画，
 *    必须在 onViewPositionChanged 方法里添加 dispatchDragEvent(newLeft)方法
 * 10、①定义状态（这里使用枚举进行定义）， 三种状态：打开  关闭  正在拖动   Status
 *     ②接口 ： 为了提供给用户在各个状态下实现相关的方法    OnLayoutDragingListener
 *     ③更新状态，在 dispatchDragEvent 里
 *     ④判断状态是否与以前的相同，不同则说明状态改变，进行回调   ViewDragHelper.Callback
 * 11、添加了动作监听，下面就要在MainActivity里实现这些监听的方法   setOnLayoutDragingListener
 */
public class DragLayout extends FrameLayout {

    private View mLeftContent;//左侧面板
    private View mMainContent;//主面板
    private int mWidth;//拖拽中显示的部分左面板的宽
    private int mHeight;//拖拽中显示的部分左面板的高
    private int mDragRange;//拖拽的最大范围
    private ViewDragHelper mDragHelper;
    private int mMainLeft;//拖动前主面板的位置
    private Status mStatus = Status.Close;
    //官方的介绍中使用了GestureDetectorCompat代替GestureDetector处理手势识别，兼容性更好
    private GestureDetectorCompat mDetectorCompat;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /**
         * 获取ViewDragHelper的实例
         * ViewDragHelper.create(ViewGroup forParent, float sensitivity, ViewDragHelper.Callback cb);
         * 参数1： 一个ViewGroup， 也就是ViewDragHelper将要用来拖拽谁下面的子view
         * 参数2：灵敏度，一般设置为1.0f就行
         * 参数3：一个回调，用来处理拖动到位置
         */
        mDragHelper = ViewDragHelper.create(this, mCallBack);
        //创建手势识别器
        mDetectorCompat = new GestureDetectorCompat(getContext(),
                mGestureListener);

    }

    private TabFragment actionBarFragment;//传递这个参数目的是想控制当在第一个tab时候左滑的时候需要判断是不是在第一个tab
    public void setBorder(TabFragment actionBarFragment){
        this.actionBarFragment = actionBarFragment;
    }

    private boolean isDrag = true;

    public void setDrag(boolean isDrag) {
        this.isDrag = isDrag;
        if(isDrag){
            //这里有个Bug,当isDrag从false变为true是，mDragHelper的mCallBack在
            //首次滑动时不响应，再次滑动才响应，只好在此调用下，让mDragHelper恢复下状态
            mDragHelper.abort();
        }
    }


    SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if((Math.abs(distanceX) > Math.abs(distanceY))&&distanceX<0&&isDrag!=false&&mStatus== Status.Close){
                if(actionBarFragment!=null){
                    if(actionBarFragment.getCurrentTabId() == MainActivity.HOME_TAB
                            || actionBarFragment.getCurrentTabId() == MainActivity.SEARCH_TAB
                            || actionBarFragment.getCurrentTabId() == MainActivity.MESSAGE_TAB
                            || actionBarFragment.getCurrentTabId() == MainActivity.FRIEND_TAB){
                        //表示第一个tab  这是处理第一个tab滑动边界的
                        if( SoftApplication.getInstance().getBorderViewPosition() != 0  ){
                            return false; //返回false表示不支持左滑切换底部view
                        }
                    }
                }
                return true;
            }else if((Math.abs(distanceX) > Math.abs(distanceY))&&distanceX>0&&isDrag!=false&&mStatus== Status.Open){
                return true;
            }else {
                return false;
            }
        }
    };

    //Callback是ViewDragHelper的抽象的静态的内部类
    ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {



        //当边缘开始拖动的时候
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mMainContent, pointerId);
        }
        // 决定哪个child是可被拖拽。返回true则进行拖拽。
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mMainContent || child == mLeftContent;
        }

        // 当capturedChild被拖拽时
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        // 横向拖拽的范围，大于0时可拖拽，等于0无法拖拽
        // 此方法只用于计算如view释放速度，敏感度等
        // 实际拖拽范围由clampViewPositionHorizontal方法设置
        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDragRange;
        }

        /**
         * 此处设置view的拖拽范围。（实际移动还未发生）
         * @param child 被拖动的视图
         * @param left  即将拖动到的位置
         * @param dx  拖动的变化量
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 拖动前oldLeft + 变化量dx == left
            if (mMainLeft + dx < 0) {
                return 0;
            } else if (mMainLeft + dx > mDragRange) {
                return mDragRange;
            }
            return left;
        }

        // 决定了当View位置改变时，希望发生的其他事情。（此时移动已经发生）
        // 高频实时的调用，在这里设置左右面板的联动
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            //如果拖动的是主面板
            if (changedView == mMainContent) {
                mMainLeft = left;
            } else {
                mMainLeft += dx;
            }

            // 进行值的修正
            if (mMainLeft < 0) {
                mMainLeft = 0;
            } else if (mMainLeft > mDragRange) {
                mMainLeft = mDragRange;
            }
            // 如果拖拽的是左面板，强制在指定位置绘制Content
            if (changedView == mLeftContent) {
                layoutContent();
            }

            dispatchDragEvent(mMainLeft);

        }

        /**
         * View被释放时,侧滑打开或恢复
         * @param releasedChild
         * @param xvel    x轴滑动的速度
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel > 0) {
                open();
            } else if (xvel == 0 && mMainLeft > mDragRange * 0.5f) {
                open();
            } else {
                close();
            }

        }

        //当拖拽状态改变的时，IDLE/DRAGGING/SETTLING（安置）
        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

    };

    private void layoutContent() {
        mMainContent.layout(mMainLeft, 0, mMainLeft + mWidth, mHeight);
        mLeftContent.layout(0, 0, mWidth, mHeight);
    }

    /**
     * 每次更新都会调用 根据当前执行的位置计算百分比percent
     */
    protected void dispatchDragEvent(int mainLeft) {
        float percent = mainLeft / (float) mDragRange;
        //伴随动画
        animViews(percent);

        if (mListener != null) {
            mListener.onDraging(percent);
        }

        Status lastStatus = mStatus;
        if (updateStatus(mainLeft) != lastStatus) {
            if (mListener == null) {
                return;
            }
            if (lastStatus == Status.Draging) {
                if (mStatus == Status.Close) {
                    mListener.onClose();
                } else if (mStatus == Status.Open) {
                    mListener.onOpen();
                }

            }
        }
    }

    public static interface OnLayoutDragingListener {
        void onOpen();

        void onClose();

        void onDraging(float percent);
    }

    private  OnLayoutDragingListener mListener;

    public void setOnLayoutDragingListener(OnLayoutDragingListener l) {
        mListener = l;
    }

    private Status updateStatus(int mainLeft) {
        if (mainLeft == 0) {
            mStatus = Status.Close;
        } else if (mainLeft == mDragRange) {
            mStatus = Status.Open;
        } else {
            mStatus = Status.Draging;
        }
        return mStatus;
    }

    public static enum Status {
        Open, Close, Draging
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    /**
     * 伴随动画：
     * @param percent
     */
    private void animViews(float percent) {
        // 主面板：缩放
        float inverse = 1 - percent * 0.2f;
        ViewHelper.setScaleX(mMainContent, inverse);
        ViewHelper.setScaleY(mMainContent, inverse);

        // 左面板：缩放、平移、透明度
        ViewHelper.setScaleX(mLeftContent, 0.5f + 0.5f * percent);
        ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * percent);

        ViewHelper.setTranslationX(mLeftContent, -mWidth / 2.0f + mWidth / 2.0f
                * percent);
        ViewHelper.setAlpha(mLeftContent, percent);
        // 背景：颜色渐变
        getBackground().setColorFilter(
                evaluate(percent, Color.BLACK, Color.TRANSPARENT),
                PorterDuff.Mode.SRC_OVER);
    }

    private int evaluate(float fraction, int startValue, int endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
                | (int) ((startB + (int) (fraction * (endB - startB))));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean onTouchEvent = mDetectorCompat.onTouchEvent(ev);
        //将Touch事件传递给ViewDragHelper
        return mDragHelper.shouldInterceptTouchEvent(ev) & onTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {
            //将Touch事件传递给ViewDragHelper
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
        }
        return true;
    }

    public void close() {
        close(true);
    }

    public void open() {
        open(true);
    }

    public void close(boolean isSmooth) {//是否通过惯性完成
        mMainLeft = 0;
        if (isSmooth) {
            // 执行动画，返回true代表有未完成的动画, 需要继续执行
            if (mDragHelper.smoothSlideViewTo(mMainContent, mMainLeft, 0)) {
                // 注意：参数传递根ViewGroup
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent();
        }
    }

    public void open(boolean isSmooth) {
        mMainLeft = mDragRange;
        if (isSmooth) {
            if (mDragHelper.smoothSlideViewTo(mMainContent, mMainLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent();
        }
    }

    @Override
    public void computeScroll() {
        // 高频率调用，决定是否有下一个变动等待执行
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        mMainContent.layout(mMainLeft, 0, mMainLeft + mWidth, mHeight);
        mLeftContent.layout(0, 0, mWidth, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //拿到宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //设置拖动范围
        mDragRange = (int) (mWidth * 0.6f);
    }

    /**
     * 填充结束时获得两个子布局的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        // 必要的检验
        if (childCount < 2) {
            throw new IllegalStateException(
                    "要有两个子视图");
        }

        if (!(getChildAt(0) instanceof ViewGroup)
                || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException(
                    "子视图必须是ViewGroup的实例");
        }

        mLeftContent = getChildAt(0);
        mMainContent = getChildAt(1);
    }

}