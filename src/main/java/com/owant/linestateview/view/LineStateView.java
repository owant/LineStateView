package com.owant.linestateview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.owant.linestateview.R;
import com.owant.linestateview.util.DensityUtils;


/**
 * Created by owant on 7/30/16.
 */
public class LineStateView extends View {

    private final int default_size = 5;
    private final int default_radius = 8;

    private int width;
    private int height;
    private Paint paint;

    private float radius;

    private int lineColor;
    private int stateColor;

    /**
     * 等级数目
     */
    private int gradeSize;

    private int currentItem = 0;
    private PointF[] itemsPoints;
    private float singleItemWidth;

    private LineStateClick listener;

    public LineStateView(Context context) {
        this(context, null, 0);
    }

    public LineStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineStateView, defStyleAttr, 0);
        int count = typedArray.getInteger(R.styleable.LineStateView_lineGradeSize, default_size);
        setGradeSize(count);

        int lColor = typedArray.getColor(R.styleable.LineStateView_lineColor, getResources().getColor(R.color.aluminum));
        setLineColor(lColor);
        int sColor = typedArray.getColor(R.styleable.LineStateView_lineStateColor, getResources().getColor(R.color.maya_blue));
        setStateColor(sColor);

        typedArray.recycle();

        radius = DensityUtils.dp2px(context, default_radius);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getLineColor());
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DensityUtils.dp2px(context, 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        itemsPoints = new PointF[getGradeSize()];
        singleItemWidth = (width * 1.0f - radius * 2) / (getGradeSize() - 1);
        for (int i = 0; i < getGradeSize(); i++) {
            itemsPoints[i] = new PointF((singleItemWidth * i) + radius, height / 2);
        }

        int middleHeight = height / 2;

        paint.setColor(getLineColor());
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(radius, middleHeight, width - radius, middleHeight, paint);

        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < getGradeSize(); i++) {
            drawCircle(canvas, i, radius);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getStateColor());
        drawCircle(canvas, currentItem, radius);

        //查看点击区
//        int touchRadius = (int) (singleItemWidth / 2);
//        for (PointF f:itemsPoints) {
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawCircle(f.x,f.y,touchRadius,paint);
//        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        setMeasuredDimension(widthVal, height);

    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {//精确数值，或者match_parent
            result = size;
        } else {
//            MeasureSpec.AT_MOST;  AT_MOST相当于warp_content
//            MeasureSpec.UNSPECIFIED; 不限制
            size = (int) radius * 2;
            if (mode == MeasureSpec.AT_MOST) {//需要约束最大的值
                //取约束的值和输入的值的最小值
                result = Math.max(result, size);
            }
        }
        return result;
    }

    private void drawCircle(Canvas canvas, int position, float radius) {
        canvas.drawCircle((singleItemWidth * position) + radius, height / 2, radius, paint);
    }

    public int getGradeSize() {
        return gradeSize;
    }

    public void setGradeSize(int gradeSize) {
        this.gradeSize = gradeSize;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getStateColor() {
        return stateColor;
    }

    public void setStateColor(int stateColor) {
        this.stateColor = stateColor;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < getGradeSize(); i++) {
                    PointF f = itemsPoints[i];
                    if (f != null) {

                        int r = (int) Math.sqrt(Math.abs(f.x - x) * Math.abs(f.x - x) + Math.abs(f.y - y) * Math.abs(f.y - y));
                        int touchRadius = (int) (singleItemWidth / 2);
                        if (r < touchRadius) {
                            currentItem = i;
                            if (listener != null) {
                                listener.onPositionClick(this, i);
                            }
                            invalidate();
                            continue;
                        }
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public void setPosition(int position) {
        currentItem = position;
        invalidate();
    }

    public void setLineStateClick(LineStateClick listener) {
        this.listener = listener;
    }

    public interface LineStateClick {
        void onPositionClick(View view, int position);
    }


}
