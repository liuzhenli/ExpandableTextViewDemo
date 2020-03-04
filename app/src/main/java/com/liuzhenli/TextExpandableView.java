package com.liuzhenli;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * describe:
 *
 * @author liuzhenli
 */
public class TextExpandableView extends LinearLayout {
    private int mDefaultTextSize = 14;
    /***折叠时文字行数*/
    private int mTextLines = 4;

    /***修正系数,android sdk 28以后,textView getLineHeight()方法获取文字行高不准确*/
    private float mTextMulty = 1.0f;

    private boolean isExpand;
    private TextView mTvExpandText;
    private ImageView mIvExpandView;

    public TextExpandableView(Context context) {
        this(context, null);
    }

    public TextExpandableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT > 27) {
            mTextMulty = 1.13f;
        }
        setOrientation(VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextExpandableView);
        String text = typedArray.getString(R.styleable.TextExpandableView_text);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.TextExpandableView_textSize, mDefaultTextSize);
        int textColor = typedArray.getDimensionPixelSize(R.styleable.TextExpandableView_textColor, Color.DKGRAY);
        mTextLines = typedArray.getInt(R.styleable.TextExpandableView_textLines, mTextLines);
        float lineSpacingMultiplier = typedArray.getFloat(R.styleable.TextExpandableView_lineSpacingMultiplier, 1.0f);
        float lineSpacingExtra = typedArray.getFloat(R.styleable.TextExpandableView_lineSpacingExtra, 0f);
        typedArray.recycle();
        mTvExpandText = new TextView(context);
        mTvExpandText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        mTvExpandText.setTextColor(textColor);
        mTvExpandText.setLineSpacing(lineSpacingExtra, lineSpacingMultiplier);
        addView(mTvExpandText);

        mIvExpandView = new ImageView(context);
        mIvExpandView.setImageResource(R.drawable.book_index_summary_arrow_down);
        addView(mIvExpandView);

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        mIvExpandView.setLayoutParams(layoutParams);
        setText(text);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mIvExpandView.getVisibility() != View.VISIBLE) {
                    //如果展开按钮不显示 不往下执行
                    return;
                }

                isExpand = !isExpand;
                mTvExpandText.clearAnimation();//清除动画效果
                //默认高度，即前边由maxLine确定的高度
                final int deltaValue;
                //起始高度
                final int startValue = (mTvExpandText.getHeight());
                //动画持续时间
                int durationMillis = 350;
                if (isExpand) {
                    /*
                     * 折叠动画
                     * 从实际高度缩回起始高度
                     */
                    deltaValue = (int) ((mTvExpandText.getLineHeight() * mTextMulty) * mTvExpandText.getLineCount() - startValue);
                    RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillis);
                    animation.setFillAfter(true);
                    mIvExpandView.startAnimation(animation);
                } else {
                    /*
                     * 展开动画
                     * 从起始高度增长至实际高度
                     */
                    deltaValue = (int) (mTvExpandText.getLineHeight() * mTextMulty * mTextLines - startValue);
                    RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillis);
                    animation.setFillAfter(true);
                    mIvExpandView.startAnimation(animation);
                }
                Animation animation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        //根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
                        mTvExpandText.setHeight((int) (startValue + deltaValue * interpolatedTime));
                    }
                };
                animation.setDuration(durationMillis);
                mTvExpandText.startAnimation(animation);
            }
        });
    }

    public void setText(String text) {
        mTvExpandText.setText(text);
        if (mTvExpandText.getLineHeight() > mTextLines) {
            mIvExpandView.setVisibility(VISIBLE);
        } else {
            mIvExpandView.setVisibility(GONE);
        }
        mTvExpandText.setHeight((int) (mTextLines * mTvExpandText.getLineHeight() * mTextMulty));
    }
}
