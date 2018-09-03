/*
 * Copyright (c) 2015-2018 Shi ChaoHui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sch.camera.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by StoneHui on 2018/8/13.
 * <p>
 * 显示对焦动画的视图。
 */
public class FocusView extends View {

    private Handler mHandler = new Handler();

    private Paint mPaint;

    private boolean isFocusing = false;
    private RectF mFocRectF;
    private int lineSize;

    private ValueAnimator animator;

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp2px(1));

        mFocRectF = new RectF();

        lineSize = dp2px(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        if (isFocusing) {
            canvas.drawCircle(mFocRectF.centerX(), mFocRectF.centerY(), mFocRectF.width() / 2, mPaint);
            canvas.drawLine(mFocRectF.left, mFocRectF.centerY(),
                    mFocRectF.left + lineSize, mFocRectF.centerY(), mPaint);
            canvas.drawLine(mFocRectF.centerX(), mFocRectF.top,
                    mFocRectF.centerX(), mFocRectF.top + lineSize, mPaint);
            canvas.drawLine(mFocRectF.right, mFocRectF.centerY(),
                    mFocRectF.right - lineSize, mFocRectF.centerY(), mPaint);
            canvas.drawLine(mFocRectF.centerX(), mFocRectF.bottom,
                    mFocRectF.centerX(), mFocRectF.bottom - lineSize, mPaint);
        }
    }

    /**
     * 聚焦。
     *
     * @param x 中心点 x 坐标。
     * @param y 中心点 y 坐标。
     */
    public void focusOn(final float x, final float y) {
        isFocusing = true;
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        // 开始聚焦动画。
        animator = ValueAnimator.ofInt(dp2px(45), dp2px(28));
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int offset = (int) valueAnimator.getAnimatedValue();
                mFocRectF.set(x - offset, y - offset, x + offset, y + offset);
                postInvalidate();
            }
        });
        animator.start();
        // 定时停止聚焦动画。
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFocusing = false;
                postInvalidate();
            }
        }, 666L);
    }

    private int dp2px(int dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

}