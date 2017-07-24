package com.test.toon.demo_contact;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义组合控件
 * 
 * @author Administrator
 *
 */
public class LetterBar extends LinearLayout{
	private OnLetterSelectedListener mListener;

	public LetterBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LetterBar(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		setBackgroundColor(Color.GRAY);
		setOrientation(VERTICAL);
		for(int i = 0; i < 26;i++) {
			TextView text = new TextView(context);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1);
			text.setLayoutParams(params);
			text.setText((char)('A'+i)+"");
			text.setTextColor(Color.WHITE);
			addView(text);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			float y = event.getY();
			int size = getHeight() / getChildCount();
			int index = (int)(y / size);
			TextView tv = (TextView) getChildAt(index);
			if(tv != null && mListener != null) {
				mListener.onLetterSelecterd(tv.getText().toString());
			}
			break;
		case MotionEvent.ACTION_UP:
			if(mListener != null) {
				mListener.onLetterSelecterd("");
			}
			break;
		default:break;
		}
		
		return true;
	}
	
	public void setOnLetterSelectedListener(OnLetterSelectedListener listener) {
		mListener = listener;
	}
	
	public interface OnLetterSelectedListener {
		void onLetterSelecterd(String str);
	}

}
