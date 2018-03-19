package com.jiangzg.ita.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.MyApp;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JZG on 2018/3/18.
 * 多个爱心上升
 */
public class GMultiLoveUpLayout extends RelativeLayout {

    private boolean mAutoStart;
    private Drawable[] mDrawables;
    private Random mRandom;
    private LayoutParams mLoveLayoutParams;
    private int dWidth;
    private int dHeight;
    private int mWidth;
    private int mHeight;
    private Interpolator[] mInterpolators;
    private Timer mTimer;
    private int mUpInterval;
    private int mPointCount;

    public GMultiLoveUpLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GMultiLoveUpLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public GMultiLoveUpLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GMultiLoveUpLayout);
        mAutoStart = a.getBoolean(R.styleable.GMultiLoveUpLayout_custom_auto_start, false);
        mUpInterval = a.getInt(R.styleable.GMultiLoveUpLayout_custom_up_interval, 200);
        mPointCount = a.getInt(R.styleable.GMultiLoveUpLayout_custom_point_count, 4);
        a.recycle();

        mRandom = new Random();
        mDrawables = new Drawable[12];
        mDrawables[0] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_blue);
        mDrawables[1] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_brown);
        mDrawables[2] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_green);
        mDrawables[3] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_grey);
        mDrawables[4] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_indigo);
        mDrawables[5] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_lime);
        mDrawables[6] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_orange);
        mDrawables[7] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_pink);
        mDrawables[8] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_purple);
        mDrawables[9] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_red);
        mDrawables[10] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_teal);
        mDrawables[11] = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_solid_yelllow);

        mInterpolators = new Interpolator[3];
        mInterpolators[0] = new AccelerateInterpolator();
        mInterpolators[1] = new DecelerateInterpolator();
        mInterpolators[2] = new AccelerateDecelerateInterpolator();

        dWidth = dHeight = ConvertUtils.dp2px(30);
        mLoveLayoutParams = new LayoutParams(dWidth, dHeight);
        mLoveLayoutParams.addRule(CENTER_HORIZONTAL);
        mLoveLayoutParams.addRule(ALIGN_PARENT_BOTTOM);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    // 外部可以 getViewTreeObserver().addOnGlobalLayoutListener
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mAutoStart) {
            startUp();
        }
    }

    public void startUp() {
        cancelUp();
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                addLove();
            }
        }, mUpInterval, mUpInterval);
    }

    public void cancelUp() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    // 开始添加爱心
    public void addLove() {
        MyApp.get().getHandler().post(new Runnable() {
            @Override
            public void run() {
                final ImageView love = new ImageView(getContext());
                love.setImageDrawable(mDrawables[mRandom.nextInt(mDrawables.length - 1)]);
                addView(love, mLoveLayoutParams);

                AnimatorSet animatorSet = getAnimatorSet(love);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        removeView(love);
                    }
                });
                animatorSet.start();
            }
        });
    }

    // 爱心动画
    private AnimatorSet getAnimatorSet(ImageView love) {
        // enterSet
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(love, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(love, "scaleY", 0f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(love, "alpha", 0f, 1f);
        AnimatorSet enterSet = new AnimatorSet();
        enterSet.setDuration(500);
        enterSet.playTogether(scaleX, scaleY, alpha);
        // bezierAnim
        ValueAnimator bezierAnimator = getBezierAnimator(love);
        // allSet
        AnimatorSet allSet = new AnimatorSet();
        allSet.playSequentially(enterSet, bezierAnimator);
        allSet.setInterpolator(mInterpolators[mRandom.nextInt(mInterpolators.length - 1)]);
        allSet.setTarget(love);
        return allSet;
    }

    // 贝塞尔曲线
    private ValueAnimator getBezierAnimator(final ImageView love) {
        // 四个点
        PointF pointF0 = new PointF((mWidth - dWidth) / 2, mHeight - dHeight);
        PointF pointF1 = getTogglePointF(1);
        PointF pointF2 = getTogglePointF(2);
        PointF pointF3 = new PointF(mRandom.nextInt(mWidth), 0);
        // 估值器
        BezierEvaluator evaluator = new BezierEvaluator(pointF1, pointF2);
        // 贝塞尔曲线动画
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, pointF0, pointF3);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                // 控制属性的变化
                love.setX(pointF.x);
                love.setY(pointF.y);
                love.setAlpha(1 - animation.getAnimatedFraction());
            }
        });

        return animator;
    }

    private PointF getTogglePointF(int index) {
        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt(mWidth);
        if (index == 1) {
            pointF.y = mRandom.nextInt(mHeight / 2) + mHeight / 2;
        } else {
            pointF.y = mRandom.nextInt(mHeight / 2);
        }
        return pointF;
    }

    // 估值器
    public class BezierEvaluator implements TypeEvaluator<PointF> {

        private PointF pointF1;
        private PointF pointF2;

        public BezierEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float fraction, PointF pointF0, PointF pointF3) {
            PointF pointF = new PointF();
            pointF.x = pointF0.x * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + 3 * pointF1.x * fraction * (1 - fraction) * (1 - fraction)
                    + 3 * pointF2.x * fraction * fraction * (1 - fraction)
                    + pointF3.x * fraction * fraction * fraction;
            pointF.y = pointF0.y * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + 3 * pointF1.y * fraction * (1 - fraction) * (1 - fraction)
                    + 3 * pointF2.y * fraction * fraction * (1 - fraction)
                    + pointF3.y * fraction * fraction * fraction;
            return pointF;
        }
    }

}
