package android.zappos.com.pointsview.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.zappos.com.pointsview.R;

public class CircularProgressBar extends View {

    private static final int DEFAULT_ANIMATION = 1500;
    private static final int DEFAULT_MAX_ANGLE = 280;
    private static final int DEFAULT_START_ANGLE = 270;
    private static final int DEFAULT_CANVAS_PADDING = 10;

    private float progress = 0;
    private float strokeWidth = 2f;
    private float backgroundStrokeWidth = 2f;
    private int currentPoints = 0;
    private int totalTierPoints = 0;
    private int size = 800;

    private int color = Color.RED;
    private int backgroundColor = Color.GRAY;

    private int maxAngle;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private Paint backgroundOverlayPaint;

    private int centerX;
    private int centerY;

    private RectF rectF = new RectF();
    private final Rect bounds = new Rect();

    ObjectAnimator animator;

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0);
        try {
            color = typedArray.getColor(R.styleable.CircularProgressBar_progressbar_color, Color.BLACK);
            backgroundColor = typedArray.getColor(R.styleable.CircularProgressBar_background_progressbar_color, Color.GRAY);
            maxAngle = typedArray.getInt(R.styleable.CircularProgressBar_max_angle, DEFAULT_MAX_ANGLE);
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircularProgressBar_progressbar_width, 2);
            backgroundStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircularProgressBar_background_progressbar_width, 2);
        } finally {
            typedArray.recycle();
        }


        backgroundPaint = new Paint();
        backgroundPaint.setDither(true);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setAntiAlias(true);

        foregroundPaint = new Paint();
        foregroundPaint.setDither(true);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setColor(color);
        foregroundPaint.setStrokeWidth(backgroundStrokeWidth);
        foregroundPaint.setAntiAlias(true);

        backgroundOverlayPaint = new Paint();
        backgroundOverlayPaint.setAntiAlias(true);
        backgroundOverlayPaint.setTextSize(100);
        backgroundOverlayPaint.setStyle(Paint.Style.FILL);
        backgroundOverlayPaint.setColor(color);
        backgroundOverlayPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rect = new RectF(0 + DEFAULT_CANVAS_PADDING, 0 + DEFAULT_CANVAS_PADDING, canvas.getWidth() - DEFAULT_CANVAS_PADDING, canvas.getHeight() - DEFAULT_CANVAS_PADDING);
        canvas.drawArc(rect, DEFAULT_START_ANGLE, maxAngle, false, backgroundPaint);
        canvas.drawArc(rect, DEFAULT_START_ANGLE, (maxAngle * (progress / 100f)), false, foregroundPaint);

        String text = currentPoints + " / " + totalTierPoints;
        backgroundOverlayPaint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, centerX - bounds.exactCenterX(), centerY - bounds.exactCenterY(), backgroundOverlayPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidthHeight = MeasureSpec.getSize(size);
        centerX = viewWidthHeight / 2;
        centerY = viewWidthHeight / 2;
        setMeasuredDimension(viewWidthHeight, viewWidthHeight);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = (progress <= 100) ? progress : 100;
        invalidate();
    }

    public void setProgress(float progress) {
        this.progress = (progress <= 100) ? progress : 100f;
        invalidate();
    }

    public float getProgressBarWidth() {
        return strokeWidth;
    }

    public CircularProgressBar setProgressBarWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        foregroundPaint.setStrokeWidth(strokeWidth);
        refresh();
        return this;
    }

    public float getBackgroundProgressBarWidth() {
        return backgroundStrokeWidth;
    }

    public CircularProgressBar setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        this.backgroundStrokeWidth = backgroundStrokeWidth;
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        refresh();
        return this;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        if (color != 0) {
            this.color = color;
            foregroundPaint.setColor(this.color);
            invalidate();
            requestLayout();
        }
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public CircularProgressBar setProgressBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        refresh();
        return this;
    }

    private void refresh() {
        invalidate();
        requestLayout();
    }

    public void setProgressWithAnimation(int progress, int currentPoints, int totalTierPoints) {
        setProgressWithAnimation(progress, DEFAULT_ANIMATION);
        this.currentPoints = currentPoints;
        this.totalTierPoints = totalTierPoints;
    }

    public void setProgressWithAnimation(int progress, int duration) {
        animator = ObjectAnimator.ofFloat(this, "progress", this.progress, progress);
        animator.setDuration(duration);
        animator.start();
    }
}