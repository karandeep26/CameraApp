package com.example.stpl.cameraapp.customViews;

/**
 * Created by stpl on 3/2/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.example.stpl.cameraapp.R;


public class CircleRectView extends ImageView {
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private int circleRadius;
    private float cornerRadius;
    private float eps = 0.001F;

    private RectF bitmapRect;
    private Path clipPath;
    private boolean fullScreen;

    public CircleRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .CircleRectView, 0, 0);
        init(a);
    }

    public CircleRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .CircleRectView, defStyleAttr, 0);
        init(a);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleRectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .CircleRectView, defStyleAttr, defStyleRes);
        init(a);
    }

    private void init(TypedArray a) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        if (a.hasValue(R.styleable.CircleRectView_circleRadius)) {
            circleRadius = a.getDimensionPixelSize(R.styleable.CircleRectView_circleRadius, 0);
            cornerRadius = circleRadius;
        }

        clipPath = new Path();
        a.recycle();
    }

    public Animator animator(int rectHeight, int rectWidth) {
        return animator(getMeasuredHeight(), getMeasuredWidth(), rectHeight, rectWidth);
    }

    public Animator animator(int startHeight, int startWidth, int endHeight, int endWidth) {
        AnimatorSet animatorSet = new AnimatorSet();

        Log.d(CircleRectView.class.getSimpleName(), "startHeight =" + startHeight
                + ", startWidth =" + startWidth
                + ", endHeight = " + endHeight
                + " endWidth =" + endWidth);

        ValueAnimator heightAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(startWidth, endWidth);

        heightAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = val;

            Log.d(CircleRectView.class.getSimpleName(), "height updated =" + val);

            setLayoutParams(layoutParams);
            requestLayoutSupport();
        });

        widthAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = val;

            setLayoutParams(layoutParams);
            requestLayoutSupport();
        });

        ValueAnimator radiusAnimator;
        if (startWidth < endWidth) {
            radiusAnimator = ValueAnimator.ofFloat(circleRadius, 0);
        } else {
            radiusAnimator = ValueAnimator.ofFloat(cornerRadius, circleRadius);
        }

        radiusAnimator.setInterpolator(new AccelerateInterpolator());
        radiusAnimator.addUpdateListener(animator -> cornerRadius = (float) (Float) animator
                .getAnimatedValue());

        animatorSet.playTogether(heightAnimator, widthAnimator, radiusAnimator);

        return animatorSet;
    }

    /**
     * this needed because of that somehow {@link #onSizeChanged} NOT CALLED when requestLayout
     * while activity transition end is running
     */
    private void requestLayoutSupport() {
        View parent = (View) getParent();
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec
                .EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec
                .EXACTLY);
        parent.measure(widthSpec, widthSpec);
        parent.layout(parent.getLeft(), parent.getTop(), parent.getRight(), parent.getBottom());
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, w, oldw, oldw);
        //This event-method provides the real dimensions of this custom view.

//        Log.d("size changed", "w = " + w + " h = " + h);

        bitmapRect = new RectF(0, 0, w, w);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        clipPath.reset();
        clipPath.addRect(bitmapRect, Path.Direction.CW);

        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }

    public void isFullScreen(boolean flag) {
        fullScreen = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);


        int width = getMeasuredWidth();

        int height = getMeasuredHeight();

        // Optimization so we don't measure twice unless we need to


        setMeasuredDimension(width, width);


    }


}



