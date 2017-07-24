package com.test.toon.demo_animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.test.toon.R;
import com.test.toon.mytestdemo.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/6.
 */

public class AnimationActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
private ImageView imageView,ivBackground;
    //    zoomView原本的宽高
    private int zoomViewWidth = 0;
    private int zoomViewHeight = 0;
    private float mScaleTimes = 2f;
    //    回弹时间系数，系数越小，回弹越快
    private float mReplyRatio = 0.5f;

    private DisplayMetrics metric;

    /**
     * 记录donw动作Y的位置
     */
    private float downY;

    /**
     * 滚动到顶部时记录位置
     */
    private float mFirstPosition;


    /**
     * CustomScrollView ontouch滑动判断
     */
    private boolean mScaling = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
         ivBackground=(ImageView)findViewById(R.id.iv_background);
        Button button_alpha= (Button) findViewById(R.id.btn_alpha);
        Button button_scale = (Button) findViewById(R.id.btn_scale);
        Button button_rotate = (Button) findViewById(R.id.btn_rotate);
        Button button_translate = (Button) findViewById(R.id.btn_translate);
        Button button_set = (Button) findViewById(R.id.btn_set);
        imageView = (ImageView) findViewById(R.id.icon);
       // imageView.setBackgroundResource();
       // imageView.setBackgroundColor();
        getScreenInit(ivBackground);
        ivBackground.setOnTouchListener(this);
        button_alpha.setOnClickListener(this);
        button_scale.setOnClickListener(this);
        button_rotate.setOnClickListener(this);
        button_translate.setOnClickListener(this);
        button_set.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_alpha :
                Animation alphaAnimation= AnimationUtils.loadAnimation(this, R.anim.alphaanim);//加载Xml文件中的动画

                imageView.startAnimation(alphaAnimation);
                break;
            case R.id.btn_scale :
                Animation scaleAnimation= AnimationUtils.loadAnimation(this, R.anim.scaleanim);//加载Xml文件中的动画

                imageView.startAnimation(scaleAnimation);
                break;
            case R.id.btn_rotate :
                Animation rotateAnimation= AnimationUtils.loadAnimation(this, R.anim.rotateanim);//加载Xml文件中的动画

                imageView.startAnimation(rotateAnimation);
                break;

            case R.id.btn_translate :
                Animation transAnimation= AnimationUtils.loadAnimation(this, R.anim.translateanim);//加载Xml文件中的动画

                imageView.startAnimation(transAnimation);
                break;
            case R.id.btn_set :
                Animation setAnimation= AnimationUtils.loadAnimation(this, R.anim.setanim);//加载Xml文件中的动画

                imageView.startAnimation(setAnimation);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (zoomViewWidth <= 0 || zoomViewHeight <= 0) {
            zoomViewWidth = ivBackground.getMeasuredWidth();
            zoomViewHeight = ivBackground.getMeasuredHeight();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mScaling = false;
                replyView(ivBackground);
                break;
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScaling) {
                    mFirstPosition = event.getY();// 滚动到顶部时记录位置，否则正常返回
                }
                int distance = (int)((event.getY() - mFirstPosition) * 0.6); // 滚动距离乘以一个系数
                if (distance < 0) {
                  //  setAnimData(svRoot.onScroll(), sivHeadSmallAvatar, isivHeadSmallAvatar, iv_more_text, ivBack, vHead, v_link);
                    break;
                } else {
                    mScaling = true;
                    // 处理放大
                    if(event.getY()-downY  > 20 ){
                            setZoom(distance, ivBackground);
                    }
                }
        }
        return true;
    }
    /**
     * 放大view
     */
    private void setZoom(float s, View zoomView) {
        float scaleTimes = (float) ((zoomViewWidth + s) / (zoomViewWidth * 1.0));
//        如超过最大放大倍数，直接返回
        if (scaleTimes > mScaleTimes) return;

        ViewGroup.LayoutParams layoutParams = zoomView.getLayoutParams();
        layoutParams.width = (int) (zoomViewWidth + s);
        layoutParams.height = (int) (zoomViewHeight * ((zoomViewWidth + s) / zoomViewWidth));
//        设置控件水平居中
        ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(-(layoutParams.width - zoomViewWidth) / 2, 0, -(layoutParams.width - zoomViewWidth) / 2, 0);
        zoomView.setLayoutParams(layoutParams);

    }
    /**
     * 回弹
     */
    private void replyView(final ImageView zoomView) {
        final float distance = zoomView.getMeasuredWidth() - zoomViewWidth;
        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(distance, 0.0F).setDuration((long) (distance * mReplyRatio));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setZoom((Float) animation.getAnimatedValue(), zoomView);
            }
        });
        anim.start();
    }
    /**
     * 获取屏幕宽高
     */
    public void getScreenInit(ImageView backgroundView) {

        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        ViewGroup.LayoutParams lp = backgroundView.getLayoutParams();
        lp.width = metric.widthPixels;
        lp.height = metric.widthPixels;
        backgroundView.setLayoutParams(lp);
    }
}
