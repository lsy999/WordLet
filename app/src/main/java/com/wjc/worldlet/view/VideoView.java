package com.wjc.worldlet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by ${万嘉诚} on 2016/10/5.
 * 微信：wjc398556712
 * 作用：
 */
public class VideoView extends android.widget.VideoView {
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 设置视频的宽和高
     * @param videoWidth
     * @param videoHeight
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams l = getLayoutParams();
        l.width = videoWidth;
        l.height = videoHeight;
        setLayoutParams(l);
    }
}
