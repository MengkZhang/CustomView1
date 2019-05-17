package com.zhang.togview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义ToggleView 完全继承View
 * <p>
 * Android中View的绘制流程
 * <p>
 * 测量                   摆放                绘制
 * measure     ->        layout       ->     draw
 * \                     \                  \
 * <p>
 * onMeasure               onLayout           onDraw  重写这些方法实现自定义控件
 * <p>
 * 这些方法都是在onResume()之后执行的
 * <p>
 * View
 * onMeasure(在这个方法指定自己的宽高)   ->  onDraw(在这个方法绘制自己的内容)
 * <p>
 * ViewGroup
 * onMeasure(在这个方法指定自己的宽高和所有子View的快高)   ->  onLayout(摆放所有子View)  ->  onDraw(在这个方法绘制自己的内容)
 */
public class ToggleView extends View {

    private Bitmap slideButtonBitmap;    //背景
    private Bitmap switchBackgroupBitmap;//滑块
    private Paint paint;                 //画笔
    private boolean mSwitchState;        //滑块的状态 默认关闭（FALSE）
    private float currentX;              //当前的x坐标

    /**
     * 用于代码创建控件
     *
     * @param context
     */
    public ToggleView(Context context) {
        super(context);
        init();
    }


    /**
     * 用于在xml中使用控件，可指定属性
     *
     * @param context
     * @param attrs
     */
    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        //获取背景
        String namespace = "http://schemas.android.com/apk/res-auto";
        int switchBackground = attrs.getAttributeResourceValue(namespace, "switch_background", -1);
        setSwitchBackgroundResource(switchBackground);

        int slideButton = attrs.getAttributeResourceValue(namespace, "slide_button", -1);
        setSlideButtonResource(slideButton);

        boolean state = attrs.getAttributeBooleanValue(namespace, "state", false);
        setSwitchState(state);
    }

    /**
     * 初始化
     */
    private void init() {
        paint = new Paint();
    }

    /**
     * 也是用于在xml中使用控件，可以指定属性，如果指定过了样式则走此构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(switchBackgroupBitmap.getWidth(), switchBackgroupBitmap.getHeight());
    }

    /**
     * 绘制方法
     *
     * @param canvas ：画布，在上面绘制的内容都会显示在界面上
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 1,绘制背景
        //left top是当前控件的左上角的坐标
        canvas.drawBitmap(switchBackgroupBitmap, 0, 0, paint);

        //2,绘制滑块
        /**2019/5/17 绘制滑块两个点：1，触摸的时候根据currentX绘制滑块
         *                           2，非触摸的时候根据开关状态的Boolean值绘制滑块
         */

        if (isTouchMode) {//根据用户滑动的位置currentX绘制滑块
//            float newLeft = (int) currentX;
            // 让滑块向左移动自身一半大小的位置
            float newLeft = currentX - slideButtonBitmap.getWidth() / 2.0f;
            int maxLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
            // 限定滑块范围
            if(newLeft < 0){
                newLeft = 0; // 左边范围
            }else if (newLeft > maxLeft) {
                newLeft = maxLeft; // 右边范围
            }
            canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);

        } else {          //根据开关状态的Boolean值绘制滑块

            if (mSwitchState) {  //开启状态 left = 背景宽度 - 滑块宽度,  top = 0
                int newLeft = switchBackgroupBitmap.getWidth() - slideButtonBitmap.getWidth();
                canvas.drawBitmap(slideButtonBitmap, newLeft, 0, paint);
            } else {            //关闭状态 left = 0, top = 0
                canvas.drawBitmap(slideButtonBitmap, 0, 0, paint);
            }
        }
    }

    boolean isTouchMode = false;//默认不是滑动状态

    /**
     * 重写触摸事件相应用户触摸
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchMode = true;//按下的时候设置为TRUE
                printLog("event:ACTION_DOWN " + event.getX());
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                printLog("event:ACTION_MOVE " + event.getX());
                currentX = event.getX();

                // TODO: 2019/5/17 目的 在 ACTION_MOVE中根据currentX值绘制滑块的位置

                break;
            case MotionEvent.ACTION_UP:
                isTouchMode = false;//松开的时候设置为FALSE
                printLog("event:ACTION_UP " + event.getX());
                currentX = event.getX();

                float center = switchBackgroupBitmap.getWidth() / 2.0f;
                // 根据当前按下的位置, 和控件中心的位置进行比较.
                boolean state = currentX > center;

                //如果开关状态变化了，状态回调, 把当前状态传出去
                if (state != mSwitchState && mOnSwitchStateUpdateListener != null) {
                    mOnSwitchStateUpdateListener.onStateUpdate(state);
                }

                mSwitchState = state;
                break;
            default:
                break;
        }

        //重绘界面
        invalidate();//会导致onDraw()被调用，界面会更新

//        return super.onTouchEvent(event); //默认不消费触摸事件
        return true;//消费了触摸事件
    }

    private void printLog(String string) {
        Log.e("===z",string);
    }

    /**
     * 设置背景图片
     *
     * @param switchBackground
     */
    public void setSwitchBackgroundResource(int switchBackground) {
        switchBackgroupBitmap = BitmapFactory.decodeResource(getResources(), switchBackground);
    }

    /**
     * 设置滑块背景图片
     *
     * @param slideButton
     */
    public void setSlideButtonResource(int slideButton) {
        slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButton);
    }

    /**
     * 设置开关的状态
     *
     * @param switchState
     */
    public void setSwitchState(boolean switchState) {
        this.mSwitchState = switchState;
    }

    //回调接口
    public interface OnSwitchStateUpdateListener {
        // 状态回调, 把当前状态传出去
        void onStateUpdate(boolean state);
    }

    private OnSwitchStateUpdateListener mOnSwitchStateUpdateListener;

    public void setOnSwitchStateUpdateListener(OnSwitchStateUpdateListener onSwitchStateUpdateListener) {
        this.mOnSwitchStateUpdateListener = onSwitchStateUpdateListener;
    }



}
