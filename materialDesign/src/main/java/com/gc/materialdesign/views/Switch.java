package com.gc.materialdesign.views;

import com.gc.materialdesign.R;
import com.gc.materialdesign.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class Switch extends CustomView {


    int backgroundColor = Color.parseColor("#4CAF50");

    Ball ball;

    boolean check = false;
    boolean eventCheck = false;
    boolean press = false;

    OnCheckedChangeListener onCheckListener;

    public Switch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (check)
                    setChecked(false);
                else
                    setChecked(true);
            }
        });
    }

    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {

        setBackgroundResource(R.drawable.background_transparent);

        // Set size of view
        setMinimumHeight(Utils.dpToPx(48, getResources()));
        setMinimumWidth(Utils.dpToPx(80 + 20, getResources()));

        //Set background Color
        // Color by resource
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
        }

        check = attrs.getAttributeBooleanValue(MATERIALDESIGNXML, "check", false);
        eventCheck = check;
        ball = new Ball(getContext());
        RelativeLayout.LayoutParams params = new LayoutParams(Utils.dpToPx(20, getResources()), Utils.dpToPx(20, getResources()));
		params.addRule(RelativeLayout.CENTER_VERTICAL);
        ball.setLayoutParams(params);
        addView(ball);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isLastTouch = true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startRedraw();
            press = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            press = false;
            isLastTouch = false;
            check = !check;
//			if(eventCheck != check){
//				eventCheck = check;
            if (onCheckListener != null)
                onCheckListener.onCheckedChanged(check);
//			}
//			if((event.getX()<= getWidth() && event.getX() >= 0) &&
//					(event.getY()<= getHeight() && event.getY() >= 0)){
            ball.animateCheck();
//			} else {
//                stopRedraw();
//            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!placedBall) {
            placeBall();
        }

        // Crop line to transparent effect
        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor((check) ? backgroundColor : Color.parseColor("#B0B0B0"));
        paint.setStrokeWidth(Utils.dpToPx(2, getResources()));
        temp.drawLine(getHeight() / 2 + 25, getHeight() / 2, getWidth() - getHeight() / 2 - 10, getHeight() / 2, paint);
        Paint transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        temp.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2, ViewHelper.getY(ball) + ball.getHeight() / 2, ball.getWidth() / 2, transparentPaint);

        canvas.drawBitmap(bitmap, 0, 0, new Paint());

        if (press) {
            paint.setColor((check) ? makePressColor() : Color.parseColor("#446D6D6D"));
            canvas.drawCircle(ViewHelper.getX(ball) + ball.getWidth() / 2, getHeight() / 2, getHeight() / 2, paint);
        }
//		invalidate();
        if (canRedraw) {
            invalidate();
        }
    }

    boolean canRedraw = false;

    private void startRedraw() {
        canRedraw = true;
        invalidate();
    }

    private void stopRedraw() {
        canRedraw = false;
        invalidate();
    }


    /**
     * Make a dark color to press effect
     *
     * @return
     */
    protected int makePressColor() {
        int r = (this.backgroundColor >> 16) & 0xFF;
        int g = (this.backgroundColor >> 8) & 0xFF;
        int b = (this.backgroundColor >> 0) & 0xFF;
        r = (r - 30 < 0) ? 0 : r - 30;
        g = (g - 30 < 0) ? 0 : g - 30;
        b = (b - 30 < 0) ? 0 : b - 30;
        return Color.argb(70, r, g, b);
    }

    // Move ball to first position in view
    boolean placedBall = false;

    private void placeBall() {
        Log.d("place","place");
//		ViewHelper.setX(ball,getHeight()/2 - ball.getWidth()/2);
//		ball.xIni = ViewHelper.getX(ball);
        ball.xIni = getHeight() / 2 - ball.getWidth() / 2 + 25;
        ball.xFin = getWidth() - getHeight() / 2 - ball.getWidth() / 2 -10;
        ball.xCen = getWidth() / 2 - ball.getWidth() / 2;
        placedBall = true;
        if (check) {
            ViewHelper.setX(ball, ball.xFin);
        } else {
            ViewHelper.setX(ball, ball.xIni);
        }
        ball.animateCheck();
    }

    //	SETTERS

    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public void setChecked(boolean check) {
        this.check = check;
        ball.animateCheck();
    }

    public boolean isChecked() {
        return check;
    }

    class Ball extends View {

        float xIni, xFin, xCen;

        public Ball(Context context) {
            super(context);
            setBackgroundResource(R.drawable.background_switch_ball_uncheck);
        }


        public void changeBackground() {
            if (check) {
                setBackgroundResource(R.drawable.background_checkbox);
                LayerDrawable layer = (LayerDrawable) getBackground();
                GradientDrawable shape = (GradientDrawable) layer.findDrawableByLayerId(R.id.shape_bacground);
                shape.setColor(backgroundColor);
            } else {
                setBackgroundResource(R.drawable.background_switch_ball_uncheck);
            }
        }

        public void animateCheck() {
            changeBackground();
            ObjectAnimator objectAnimator;
            if (check) {
                objectAnimator = ObjectAnimator.ofFloat(this, "x", ball.xFin);
            } else {
                objectAnimator = ObjectAnimator.ofFloat(this, "x", ball.xIni);
            }
            objectAnimator.addListener(animatorListener);
            objectAnimator.setDuration(300);
            objectAnimator.start();
        }

        public void animateCheck1() {
            changeBackground();
            ObjectAnimator objectAnimator;
            if (check) {
                objectAnimator = ObjectAnimator.ofFloat(this, "x", ball.xFin);
            } else {
                objectAnimator = ObjectAnimator.ofFloat(this, "x", ball.xIni);
            }
            objectAnimator.addListener(animatorListener);
            objectAnimator.setDuration(0);
            objectAnimator.start();
        }
    }

    private AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animation) {
            stopRedraw();
        }

        ;
    };

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public interface OnCheckedChangeListener {
        public void onCheckedChanged(boolean isChecked);
    }

}
