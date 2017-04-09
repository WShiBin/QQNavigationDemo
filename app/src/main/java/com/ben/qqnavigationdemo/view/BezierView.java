package com.ben.qqnavigationdemo.view;

import android.widget.FrameLayout;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ben.qqnavigationdemo.R;

/**
 * *********************************************************
 * <pre>
 * PROJECT: QQNavigationDemo
 * INTRODUCATION: //todo
 * DESCRIPTION: //todo
 * DATE: 2017/04/3:03 PM
 * AUTHOR: shibin1990
 * Email: shib90@qq.com
 * From: https://github.com/chenupt/BezierDemo
 * </pre>
 * *********************************************************
 */

public class BezierView extends FrameLayout {
    // 默认定点圆半径
    public static final float DEFAULT_RADIUS = 20;

    private Paint paint;
    private Path path;

    // 手势坐标
    float x = 300;
    float y = 300;

    // 锚点坐标
    float anchorX = 200;
    float anchorY = 300;

    // 起点坐标
    float startX = 100;
    float startY = 100;

    // 定点圆半径
    float radius = DEFAULT_RADIUS;

    // 判断动画是否开始
    boolean isAnimStart;
    // 判断是否开始拖动
    boolean isTouch;

    ImageView exploredImageView;
    ImageView tipImageView;

    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        path = new Path();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        exploredImageView = new ImageView(getContext());
        exploredImageView.setLayoutParams(params);
        exploredImageView.setImageResource(R.drawable.tip_anim);
        exploredImageView.setVisibility(View.INVISIBLE);

        tipImageView = new ImageView(getContext());
        tipImageView.setLayoutParams(params);
        tipImageView.setImageResource(R.drawable.skin_tips_newmessage_ninetynine);

        addView(tipImageView);
        addView(exploredImageView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        exploredImageView.setX(startX - exploredImageView.getWidth() / 2);
        exploredImageView.setY(startY - exploredImageView.getHeight() / 2);

        tipImageView.setX(startX - tipImageView.getWidth() / 2);
        tipImageView.setY(startY - tipImageView.getHeight() / 2);

        super.onLayout(changed, left, top, right, bottom);
    }


    private void calculate() {
        float distance = (float) Math.sqrt(Math.pow(y - startY, 2) + Math.pow(x - startX, 2));
        radius = -distance / 15 + DEFAULT_RADIUS;

        if (radius < 9) {
            isAnimStart = true;

            exploredImageView.setVisibility(View.VISIBLE);
            exploredImageView.setImageResource(R.drawable.tip_anim);
            ((AnimationDrawable) exploredImageView.getDrawable()).stop();
            ((AnimationDrawable) exploredImageView.getDrawable()).start();

            tipImageView.setVisibility(View.GONE);
        }

        // 根据角度算出四边形的四个点
        float offsetX = (float) (radius * Math.sin(Math.atan((y - startY) / (x - startX))));
        float offsetY = (float) (radius * Math.cos(Math.atan((y - startY) / (x - startX))));

        float x1 = startX - offsetX;
        float y1 = startY + offsetY;

        float x2 = x - offsetX;
        float y2 = y + offsetY;

        float x3 = x + offsetX;
        float y3 = y - offsetY;

        float x4 = startX + offsetX;
        float y4 = startY - offsetY;

        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(anchorX, anchorY, x2, y2);
        path.lineTo(x3, y3);
        path.quadTo(anchorX, anchorY, x4, y4);
        path.lineTo(x1, y1);

        // 更改图标的位置
        tipImageView.setX(x - tipImageView.getWidth() / 2);
        tipImageView.setY(y - tipImageView.getHeight() / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isAnimStart || !isTouch) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
        } else {
            calculate();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
            canvas.drawPath(path, paint);
            canvas.drawCircle(startX, startY, radius, paint);
            canvas.drawCircle(x, y, radius, paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 判断触摸点是否在tipImageView中
            Rect rect = new Rect();
            int[] location = new int[2];
            tipImageView.getDrawingRect(rect);
            tipImageView.getLocationOnScreen(location);
            rect.left = location[0];
            rect.top = location[1];
            rect.right = rect.right + location[0];
            rect.bottom = rect.bottom + location[1];
            if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                isTouch = true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouch = false;
            tipImageView.setX(startX - tipImageView.getWidth() / 2);
            tipImageView.setY(startY - tipImageView.getHeight() / 2);
        }
        invalidate();
        if (isAnimStart) {
            return super.onTouchEvent(event);
        }
        anchorX = (event.getX() + startX) / 2;
        anchorY = (event.getY() + startY) / 2;
        x = event.getX();
        y = event.getY();
        return true;
    }
}
