package com.dunn.tools.mydraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dunn.tools.R;

/**
 * Author:Administrator
 * Date:2021/9/26 15:52
 * Description:PorterDuff.Mode 测试
 */
public class PorterDuffXfermodeView extends View {
    private Paint mPaint;
    private Bitmap dstBmp, srcBmp;
    private RectF dstRect, srcRect;

    private Xfermode mXfermode;
    private PorterDuff.Mode mPorterDuffMode = PorterDuff.Mode.SRC_ATOP;

    public PorterDuffXfermodeView(Context context) {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        dstBmp = BitmapFactory.decodeResource(getResources(), R.drawable.source_hdmi_no_focus);
        srcBmp = BitmapFactory.decodeResource(getResources(), R.drawable.source_hdmi_focus);
        mXfermode = new PorterDuffXfermode(mPorterDuffMode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景色设为白色，方便比较效果
        canvas.drawColor(Color.RED);
        //将绘制操作保存到新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
        int saveCount = canvas.saveLayer(srcRect, mPaint, Canvas.ALL_SAVE_FLAG);
        //绘制目标图
        canvas.drawBitmap(dstBmp, null, dstRect, mPaint);
        //设置混合模式
        mPaint.setXfermode(mXfermode);
        //绘制源图
        canvas.drawBitmap(srcBmp, null, srcRect, mPaint);
        //清除混合模式
        mPaint.setXfermode(null);
        //还原画布
        canvas.restoreToCount(saveCount);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //设置背景色
//        canvas.drawARGB(255, 139, 197, 186);
//
//        int canvasWidth = canvas.getWidth();
//        int canvasHeight = canvas.getHeight();
//        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
//        int r = canvasWidth / 3;
//        //正常绘制黄色的圆形
//        mPaint.setColor(getResources().getColor(R.color.draw_test_yellow));
//        canvas.drawCircle(r, r, r, mPaint);
//        //使用CLEAR作为PorterDuffXfermode绘制蓝色的矩形
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
//        mPaint.setColor(getResources().getColor(R.color.draw_test_bule));
//        canvas.drawRect(r, r, r * 2.7f, r * 2.7f, mPaint);
//        //最后将画笔去除Xfermode
//        mPaint.setXfermode(null);
//        canvas.restoreToCount(layerId);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = w <= h ? w : h;
        int centerX = w/2;
        int centerY = h/2;
        int quarterWidth = width /4;
        srcRect = new RectF(centerX-quarterWidth, centerY-quarterWidth, centerX+quarterWidth, centerY+quarterWidth);
        dstRect = new RectF(centerX-quarterWidth, centerY-quarterWidth, centerX+quarterWidth, centerY+quarterWidth);
    }
}
