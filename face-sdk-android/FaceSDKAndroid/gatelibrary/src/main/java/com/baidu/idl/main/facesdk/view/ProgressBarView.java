package com.baidu.idl.main.facesdk.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.baidu.idl.main.facesdk.BuildConfig;
import com.baidu.idl.main.facesdk.gatelibrary.R;


/**
 * Created by v_shishuaifeng on 2020/2/10.
 */

public class ProgressBarView extends View {
    private static final String TAG = ProgressBarView.class.getSimpleName();
    private Context mContext;

    // 圆心坐标
    private Point mCenterPoint;
    private float mRadius;

    private boolean antiAlias;

    // 绘制数值
    public float mMaxValue;


    public float mValue;

    // 前景圆弧
    private Paint mArcPaint;
    private float mArcWidth;
    // 刻度之间的间隔
    private int mDialIntervalDegree;
    private float mStartAngle;
    private float mSweepAngle;
    private RectF mRectF;
    // 渐变
    private int[] mGradientColors = {Color.parseColor("#0DC7FF"),
            Color.parseColor("#0D9EFF"), Color.parseColor("#0DC7FF")};
    // 当前进度，[0.0f,1.0f]
    private float mPercent;
    // 动画时间
    private long mAnimTime;
    // 属性动画
    private ValueAnimator mAnimator;

    // 背景圆弧
    private Paint mBgArcPaint;
    private int mBgArcColor;

    // 刻度线颜色
    private Paint mDialPaint;
    private float mDialWidth;
    private int mDialColor;
    public String DialColor = "#999999";
    private int mDefaultSize;

    public ProgressBarView(Context context) {
        super(context);
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize = MiscUtil.dipToPx(context, Constant.DEFAULT_SIZE);
        mRectF = new RectF();
        mCenterPoint = new Point();
        initConfig(context, attrs);
        initPaint();
        setValue(mValue);
    }

    private void initConfig(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialProgress);

        // 抗锯齿开关
        antiAlias = typedArray.getBoolean(R.styleable.DialProgress_antiAlias, true);
        // 设置圆形
        mMaxValue = typedArray.getFloat(R.styleable.DialProgress_maxValue, Constant.DEFAULT_MAX_VALUE);
        // mValue = typedArray.getFloat(R.styleable.DialProgress_value, Constant.DEFAULT_VALUE);

        mDialIntervalDegree = typedArray.getInt(R.styleable.DialProgress_dialIntervalDegree, 10);

        mArcWidth = typedArray.getDimension(R.styleable.DialProgress_arcWidth, Constant.DEFAULT_ARC_WIDTH);

        mStartAngle = typedArray.getFloat(R.styleable.DialProgress_startAngle, Constant.DEFAULT_START_ANGLE);
        mSweepAngle = typedArray.getFloat(R.styleable.DialProgress_sweepAngle, Constant.DEFAULT_SWEEP_ANGLE);
        // 设置动画时间
        mAnimTime = typedArray.getInt(R.styleable.DialProgress_animTime, Constant.DEFAULT_ANIM_TIME);

        mBgArcColor = typedArray.getColor(R.styleable.DialProgress_bgArcColor, Color.GRAY);
        mDialWidth = typedArray.getDimension(R.styleable.DialProgress_dialWidth, 2);
        mDialColor = typedArray.getColor(R.styleable.DialProgress_dialColor, Color.parseColor(DialColor));

        int gradientArcColors = typedArray.getResourceId(R.styleable.DialProgress_arcColors, 0);
        if (gradientArcColors != 0) {
            try {
                int[] gradientColors = getResources().getIntArray(gradientArcColors);
                if (gradientColors.length == 0) {
                    int color = getResources().getColor(gradientArcColors);
                    mGradientColors = new int[2];
                    mGradientColors[0] = color;
                    mGradientColors[1] = color;
                } else if (gradientColors.length == 1) {
                    mGradientColors = new int[2];
                    mGradientColors[0] = gradientColors[0];
                    mGradientColors[1] = gradientColors[0];
                } else {
                    mGradientColors = gradientColors;
                }
            } catch (Resources.NotFoundException e) {
                throw new Resources.NotFoundException("the give resource not found.");
            }
        }
        typedArray.recycle();
    }

    private void initPaint() {
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        mArcPaint.setStrokeCap(Paint.Cap.BUTT);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.BUTT);
        mBgArcPaint.setColor(mBgArcColor);

        mDialPaint = new Paint();
        mDialPaint.setAntiAlias(antiAlias);
        mDialPaint.setStrokeWidth(mDialWidth);
    }

    /**
     * 更新圆弧画笔
     */
    private void updateArcPaint() {
        // 设置渐变
        // 渐变的颜色是360度，如果只显示270，那么则会缺失部分颜色
        SweepGradient sweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y, mGradientColors, null);
        mArcPaint.setShader(sweepGradient);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MiscUtil.measure(widthMeasureSpec, mDefaultSize),
                MiscUtil.measure(heightMeasureSpec, mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: w = " + w + "; h = " + h + "; oldw = " + oldw + "; oldh = " + oldh);
        int minSize = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - 2 * (int) mArcWidth,
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - 2 * (int) mArcWidth);
        mRadius = minSize / 2;
        mCenterPoint.x = getMeasuredWidth() / 2;
        mCenterPoint.y = getMeasuredHeight() / 2;
        // 绘制圆弧的边界
        mRectF.left = mCenterPoint.x - mRadius - mArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - mArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + mArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + mArcWidth / 2;

        updateArcPaint();
        Log.d(TAG, "onMeasure: 控件大小 = " + "(" + getMeasuredWidth() + ", " + getMeasuredHeight() + ")"
                + ";圆心坐标 = " + mCenterPoint.toString()
                + ";圆半径 = " + mRadius
                + ";圆的外接矩形 = " + mRectF.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawDial(canvas);
    }

    private void drawArc(Canvas canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
        float currentAngle = mSweepAngle * mPercent;
        canvas.save();
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        // canvas.drawArc(mRectF, 0, mSweepAngle - currentAngle, false, mArcPaint);
        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则相当于 startAngle % 360
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        canvas.drawArc(mRectF, 0, currentAngle, false, mArcPaint);
        canvas.restore();
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawDial(Canvas canvas) {
        // 获取分成多少个间隔
        int total = (int) (mSweepAngle / mDialIntervalDegree);
        canvas.save();
        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);
        mDialPaint.setColor(Color.parseColor(DialColor));
        for (int i = 0; i <= total; i++) {
            // 这一点可能比较难理解点:drawLine(...)从表面看画的是圆最右边的一条白线(白色小矩形),但是由于在drawArc()中已经将canvas顺时针旋转了135度,一次刻度间隔的白线也就从圆弧起点开始了
            canvas.drawLine(
                    mCenterPoint.x + mRadius,
                    mCenterPoint.y,
                    mCenterPoint.x + mRadius + mArcWidth,
                    mCenterPoint.y,
                    mDialPaint);
            canvas.rotate(mDialIntervalDegree, mCenterPoint.x, mCenterPoint.y);
        }
        canvas.restore();
    }

    /**
     * 设置当前值
     *
     * @param value
     */
    public void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent
                            + ";currentAngle = " + (mSweepAngle * mPercent)
                            + ";value = " + mValue);
                }
                invalidate();
            }
        });
        mAnimator.start();
    }
}
